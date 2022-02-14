package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.model.*
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject
import kotlin.random.Random


private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    authorId = 0,
    likedByMe = false,
    likes = 0,
    published = "",
    newer =0,
    attachment = Attachment (
        url = "http://10.0.2.2:9999/media/d7dff806-4456-4e35-a6a1-9f2278c5d639.png",
        type = AttachmentType.IMAGE
    )
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    auth: AppAuth,
) : ViewModel() {
    private val cached: Flow<PagingData<FeedItem>> = repository
        .data
        .map { pagingData ->
            pagingData.insertSeparators(
                generator = { before, after ->
                    if (before?.id?.rem(7) != 0L)

                        Timing(
                            Random.nextLong(),
                            ""
                        )
                    else

                        Ad(
                            Random.nextLong(),
                            "https://netology.ru",
                            "figma.jpg",
                            Timing(0, "")
                        )
                }
            )


        }
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<FeedItem>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached
                .map { pagingData ->
                    pagingData.map { item ->
                        if (item !is Post) item else item.copy(ownedByMe = item.authorId == myId)
                    }
                }
        }

            //  val data: Flow<PagingData<FeedItem>>  =repository.data
            //  .cachedIn(viewModelScope)

            //.map { pagingData ->
            //если данные рекламы будут приходить отдельно по сети,
            // то делать это нужно в репозитории, связав два Flow операторами):


//                        pagingData.insertSeparators(
//                            generator = { before, after ->
//                                if (before?.id?.rem(7) != 0L)
//
//                                    Timing(
//                                        Random.nextLong(),
//                                        ""
//                                    )
//                                else
//
//                                    Ad(
//                                        Random.nextLong(),
//                                        "https://netology.ru",
//                                        "figma.jpg",
//                                        Timing(0, "")
//                                    )
//                            }
//                        )
//
//                    }
            //           }

            private val _dataState = MutableLiveData<FeedModelState>()
            val dataState: LiveData<FeedModelState>
            get() = _dataState

            private val edited = MutableLiveData(empty)
            private val _postCreated = SingleLiveEvent<Unit>()
            val postCreated: LiveData<Unit>
            get() = _postCreated

            private val noPhoto = PhotoModel()
            private val _photo = MutableLiveData(noPhoto)
            val photo: LiveData<PhotoModel>
            get() = _photo

// LiveData<ErrorModel> (для обработки ошибки)
            private val _error = SingleLiveEvent<ErrorModel>()
            val error: LiveData<ErrorModel>
            get() = _error
//
//работает с  feedmodel
//        switchMap позволяет нам подписаться на изменения data и на основании этого получить новую LiveData.
//     Т. е.  «предыдущему» Flow будет отправлен cancel, что приведёт к выбросу CancellationException.
//    val newerCount: LiveData<Int> = data.switchMap {
//        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
//            .catch { e -> e.printStackTrace() }
//            .asLiveData(Dispatchers.Default)
//    }

// подписаться на id первого поста в БД
            val newerCount: Flow<Int> = repository.getFirstPostId()
                .flatMapLatest {
                    repository.getNewerCount(it ?: 0)
                }

            init {

                loadPosts()
            }

            fun loadPosts() = viewModelScope.launch {
                try {
                    _dataState.value = FeedModelState(loading = true)
                    // repository.stream.cachedIn(viewModelScope).
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }

            fun refreshPosts() = viewModelScope.launch {
                try {
                    _dataState.value = FeedModelState(refreshing = true)
//            repository.getAll()
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }

            fun save() {
                edited.value?.let {
                    _postCreated.value = Unit
                    viewModelScope.launch {
                        try {
                            when (_photo.value) {
                                noPhoto -> repository.save(it)
                                else -> _photo.value?.file?.let { file ->
                                    repository.saveWithAttachment(it, MediaUpload(file))
                                }
                            }
                            _dataState.value = FeedModelState()
                        } catch (e: Exception) {
                            _dataState.value = FeedModelState(error = true)
                        }
                    }
                }
                edited.value = empty
                _photo.value = noPhoto
            }

            fun edit(post: Post) {
                edited.value = post

            }

            fun changeContent(content: String) {
                val text = content.trim()
                if (edited.value?.content == text) {
                    return
                }
                edited.value = edited.value?.copy(content = text)
            }

            fun changePhoto(uri: Uri?, file: File?) {
                _photo.value = PhotoModel(uri, file)
            }

            fun likeById(id: Long) = viewModelScope.launch {
                try {
                    repository.likeById(id)
                } catch (e: Exception) {
                    _error.postValue(
                        ErrorModel(
                            ErrorType.AppError,
                            ActionType.Like,
                            e.message ?: ""
                        )
                    )
                    //_data.postValue(FeedModel(error = true))
                }
            }

            fun unlikeById(id: Long) = viewModelScope.launch {
                try {
                    repository.unlikeById(id)


                } catch (e: Exception) {
                    _error.postValue(
                        ErrorModel(
                            ErrorType.AppError,
                            ActionType.Like,
                            e.message ?: ""
                        )
                    )
                    //_data.postValue(FeedModel(error = true))
                }
            }


            fun removeById(id: Long) = viewModelScope.launch {
                try {
                    repository.removeById(id)
                } catch (e: Exception) {

                    _error.postValue(
                        ErrorModel(
                            ErrorType.NetworkError,
                            ActionType.RemoveById, e.message ?: ""
                        )
                    )

                    //_data.postValue(_data.value?.copy(posts = old))
                }

            }

            fun countMessegePost() = viewModelScope.launch {
                try {
                    repository.countMessegePost()
                } catch (e: Exception) {

                    _error.postValue(
                        ErrorModel(
                            ErrorType.NetworkError,
                            ActionType.CountMessegePost, e.message ?: ""
                        )
                    )
                    //_data.postValue(_data.value?.copy(posts = old))
                }
            }


            fun unCountNewer() = viewModelScope.launch {

                try {
                    repository.unCountNewer()
                } catch (e: Exception) {

                    _error.postValue(
                        ErrorModel(
                            ErrorType.NetworkError,
                            ActionType.UnCountMessegePost, e.message ?: ""
                        )
                    )
                }
            }

        }


