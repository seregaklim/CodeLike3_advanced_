package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.http.Field
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.model.*
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

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
        val data: LiveData<FeedModel> = auth.authStateFlow
            .flatMapLatest { (myId, _) ->
                repository.data
                    .map { posts ->
                        FeedModel(
                            posts.map { it.copy(ownedByMe = it.authorId == myId) },
                            posts.isEmpty()
                        )
                    }
            }.asLiveData(Dispatchers.Default)



    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    //    switchMap позволяет нам подписаться на изменения data и на основании этого получить новую LiveData.
//     Т. е.  «предыдущему» Flow будет отправлен cancel, что приведёт к выбросу CancellationException.
    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    // LiveData<ErrorModel> (для обработки ошибки)
    private val _error = SingleLiveEvent<ErrorModel>()
    val error: LiveData<ErrorModel>
        get() = _error

    private val noPhoto = PhotoModel()
    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
    }


    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _error.postValue(
                ErrorModel(
                    ErrorType.NetworkError, ActionType.GetAll,
                    e.message ?: "",
                )
            )
            // _dataState.value = FeedModelState(error = true)

        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _error.postValue(
                ErrorModel(
                    ErrorType.NetworkError, ActionType.Refresh,
                    e.message ?: "",
                )
            )
            //  _dataState.value = FeedModelState(error = true)
        }
    }
    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when(_photo.value) {
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

    fun edit(post: Post){
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
            _error.postValue(ErrorModel(ErrorType.AppError, ActionType.Like, e.message ?: ""))
            //_data.postValue(FeedModel(error = true))
        }
    }

    fun unlikeById(id: Long) = viewModelScope.launch {
        try {
            repository.unlikeById(id)


        } catch (e: Exception) {
            _error.postValue(ErrorModel(ErrorType.AppError, ActionType.Like, e.message ?: ""))
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

    fun countMessegePost ()= viewModelScope.launch {
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


    fun unCountNewer()= viewModelScope.launch {

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


