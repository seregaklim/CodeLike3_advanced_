package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.map
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.*
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    val data: LiveData<FeedModel> = repository.data.map(::FeedModel)
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    // LiveData<ErrorModel> (для обработки ошибки)
    private val _error = SingleLiveEvent<ErrorModel>()
    val error: LiveData<ErrorModel>
        get() = _error

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
                    repository.save(it)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _error.postValue(
                        ErrorModel(
                            ErrorType.NetworkError,
                            ActionType.Save, e.message ?: "Не сохранился"
                        )
                    )
                    edited.postValue(empty)

                    // _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
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


    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
            }

        catch (e: Exception) {
            _error.postValue(ErrorModel(ErrorType.AppError, ActionType.Like, e.message ?: ""))
            //_data.postValue(FeedModel(error = true))
        }
    }
fun dislikeById (id: Long) = viewModelScope.launch {
    try {
        repository.dislikeById(id)


    } catch (e: Exception) {
        _error.postValue(ErrorModel(ErrorType.AppError, ActionType.Like, e.message ?: ""))
        //_data.postValue(FeedModel(error = true))
    }
}


fun removeById(id:Long) = viewModelScope.launch {
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

}







//
//
//
//package ru.netology.nmedia.viewmodel
//
//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.model.FeedModel
//import ru.netology.nmedia.repository.PostRepository
//import ru.netology.nmedia.repository.PostRepositoryImpl
//import ru.netology.nmedia.util.SingleLiveEvent
//
//private val empty = Post(
//    id = 0,
//    content = "",
//    author = "",
//    authorAvatar = "",
//    likedByMe = false,
//    likes = 0,
//    published = ""
//)
//
//class PostViewModel(application: Application) : AndroidViewModel(application) {
//    // упрощённый вариант
//    private val repository: PostRepository = PostRepositoryImpl()
//    private val _data = MutableLiveData(FeedModel())
//    val data: LiveData<FeedModel>
//        get() = _data
//    private val edited = MutableLiveData(empty)
//    private val _postCreated = SingleLiveEvent<Unit>()
//    val postCreated: LiveData<Unit>
//        get() = _postCreated
//
//    init {
//        loadPosts()
//    }
//
//    fun loadPosts() {
//        _data.value = FeedModel(loading = true)
//        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
//            override fun onSuccess(posts: Post) {
//                _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
//            }
//
//            override fun onError(e: Exception) {
//                _data.value = FeedModel(error = true)
//            }
//        })
//    }
//
//    fun refresh() {
//        _data.value = FeedModel(refreshing = true)
//        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
//            override fun onSuccess(posts: Post) {
//                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//
//            }
//
//            override fun onError(e: Exception) {
//                _data.postValue(FeedModel(error = true))
//            }
//        })
//    }
//
//
//    fun save() {
//        edited.value?.let {
//            repository.saveAsync(it, object : PostRepository.Callback<Post> {
//                override fun onSuccess(posts: Post) {
//                    _postCreated.postValue(Unit)
//                    edited.postValue(empty)
//                }
//
//                override fun onError(e: Exception) {
//                    edited.postValue(empty)
//                }
//            })
//        }
//    }
//
//    fun changeContent(content: String) {
//        val text = content.trim()
//        if (edited.value?.content == text) {
//            return
//        }
//        edited.value = edited.value?.copy(content = text)
//    }
//
//    fun edit(post: Post) {
//        edited.value = post
//    }
//
//
//    fun removeById(id: Long) {
//        repository.removeByIdAsync(id, object : PostRepository.Callback<Unit> {
//            val old = _data.value?.posts.orEmpty()
//            override fun onSuccess(posts: Post) {
//
//                _data.postValue(
//                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                        .filter { it.id != id }
//                    )
//                )
//            }
//
//            override fun onError(e: Exception) {
//                _data.postValue(_data.value?.copy(posts = old))
//
//            }
//        })
//    }
//
//}















//package ru.netology.nmedia.viewmodel
//
//import android.app.Application
//import androidx.lifecycle.*
//import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.model.FeedModel
//import ru.netology.nmedia.repository.*
//import ru.netology.nmedia.util.SingleLiveEvent
//
//private val empty = Post(
//    id = 0,
//    author ="",
//    content = "",
//    likedByMe = false,
//    likes = 0,
//    published = "",
//    authorAvatar ="",
//)
//
//class PostViewModel(application: Application) : AndroidViewModel(application) {
//    // упрощённый вариант
//    private val repository: PostRepository = PostRepositoryImpl()
//    private val _data = MutableLiveData(FeedModel())
//    val data: LiveData<FeedModel>
//        get() = _data
//    val edited = MutableLiveData(empty)
//    private val _postCreated = SingleLiveEvent<Unit>()
//    val postCreated: LiveData<Unit>
//        get() = _postCreated
//
//
//    init {
//        loadPosts()
//    }
//
//    fun loadPosts() {
//        _data.value = FeedModel(loading = true)
//        repository.getAllAsync(object : PostRepository.GetAllCallback {
//            override fun onSuccess(posts: List<Post>) {
//                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//            }
//
//            override fun onError(e: Exception) {
//                _data.postValue(FeedModel(error = true))
//            }
//        })
//    }
//
//    fun refresh() {
//        _data.value = FeedModel(refreshing = true)
//        repository.getAllAsync(object : PostRepository.GetAllCallback {
//            override fun onSuccess(posts: List<Post>) {
//                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//
//            }
//
//            override fun onError(e: Exception) {
//                _data.postValue(FeedModel(error = true))
//            }
//        })
//    }
//
//    fun save() {
//        edited.value?.let {
//            repository.saveAsync(it, object : PostRepository.SaveCallback {
//                override fun onSuccess(posts: Post) {
//                    _postCreated.postValue(Unit)
//                    edited.postValue(empty)
//                }
//
//                override fun onError(e: Exception) {
//                    edited.postValue(empty)
//                }
//            })
//        }
//    }
//
//    fun changeContent(content: String) {
//        val text = content.trim()
//        if (edited.value?.content == text) {
//            return
//        }
//        edited.value = edited.value?.copy(content = text)
//    }
//
//    fun edit(post: Post) {
//        edited.value = post
//    }
//
//    fun removeById(id: Long) {
//
//        repository.removeByIdAsync(id, object : PostRepository.removeByIdCallback {
//            val old = _data.value?.posts.orEmpty()
//            override fun onSuccess(id: Long) {
//
//                _data.postValue(
//                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                        .filter { it.id != id }
//                    )
//                )
//            }
//
//            override fun onError(e: Exception) {
//                _data.postValue(_data.value?.copy(posts = old))
//
//            }
//        })
//    }
//
//    fun likeById(id: Long) {
//        repository.likeByIdSync(id, object : PostRepository.likeByIdCallback {
//            override fun onSuccess(post: Post) {
//                _data.postValue(
//                    FeedModel(
//                        posts = _data.value?.posts.orEmpty().map {
//                            if (it.id == post.id) post else it
//                        }
//                    )
//                )
//            }
//            override fun onError(e: Exception) {
//                _data.postValue(FeedModel(error = true))
//            }
//        })
//    }
//
//    fun unlikeById(id: Long) {
//        repository.unlikeByIdAsync(id, object : PostRepository.unlikeByCallback {
//            override fun onSuccess(post: Post) {
//
//                _data.postValue(
//                    FeedModel(
//                        posts = _data.value?.posts.orEmpty().map {
//                            if (it.id == post.id) post else it
//                        }
//                    )
//                )
//            }
//            override fun onError(e: Exception) {
//                _data.postValue(FeedModel(error = true))
//            }
//        })
//    }
//}

//
//
//
//package ru.netology.nmedia.viewmodel
//
//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.model.FeedModel
//import ru.netology.nmedia.repository.PostRepository
//import ru.netology.nmedia.repository.PostRepositoryImpl
//import ru.netology.nmedia.util.SingleLiveEvent
//
//private val empty = Post(
//    id = 0,
//    content = "",
//    author = "",
//    authorAvatar = "",
//    likedByMe = false,
//    likes = 0,
//    published = ""
//)
//
//class PostViewModel(application: Application) : AndroidViewModel(application) {
//    // упрощённый вариант
//    private val repository: PostRepository = PostRepositoryImpl()
//    private val _data = MutableLiveData(FeedModel())
//    val data: LiveData<FeedModel>
//        get() = _data
//    private val edited = MutableLiveData(empty)
//    private val _postCreated = SingleLiveEvent<Unit>()
//    val postCreated: LiveData<Unit>
//        get() = _postCreated
//
//    init {
//        loadPosts()
//    }
//
//    fun loadPosts() {
//        _data.value = FeedModel(loading = true)
//        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
//            override fun onSuccess(posts: Post) {
//                _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
//            }
//
//            override fun onError(e: Exception) {
//                _data.value = FeedModel(error = true)
//            }
//        })
//    }
//
//    fun refresh() {
//        _data.value = FeedModel(refreshing = true)
//        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
//            override fun onSuccess(posts: Post) {
//                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//
//            }
//
//            override fun onError(e: Exception) {
//                _data.postValue(FeedModel(error = true))
//            }
//        })
//    }
//
//
//    fun save() {
//        edited.value?.let {
//            repository.saveAsync(it, object : PostRepository.Callback<Post> {
//                override fun onSuccess(posts: Post) {
//                    _postCreated.postValue(Unit)
//                    edited.postValue(empty)
//                }
//
//                override fun onError(e: Exception) {
//                    edited.postValue(empty)
//                }
//            })
//        }
//    }
//
//    fun changeContent(content: String) {
//        val text = content.trim()
//        if (edited.value?.content == text) {
//            return
//        }
//        edited.value = edited.value?.copy(content = text)
//    }
//
//    fun edit(post: Post) {
//        edited.value = post
//    }
//
//
//    fun removeById(id: Long) {
//        repository.removeByIdAsync(id, object : PostRepository.Callback<Unit> {
//            val old = _data.value?.posts.orEmpty()
//            override fun onSuccess(posts: Post) {
//
//                _data.postValue(
//                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                        .filter { it.id != id }
//                    )
//                )
//            }
//
//            override fun onError(e: Exception) {
//                _data.postValue(_data.value?.copy(posts = old))
//
//            }
//        })
//    }
//
//}















//package ru.netology.nmedia.viewmodel
//
//import android.app.Application
//import androidx.lifecycle.*
//import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.model.FeedModel
//import ru.netology.nmedia.repository.*
//import ru.netology.nmedia.util.SingleLiveEvent
//
//private val empty = Post(
//    id = 0,
//    author ="",
//    content = "",
//    likedByMe = false,
//    likes = 0,
//    published = "",
//    authorAvatar ="",
//)
//
//class PostViewModel(application: Application) : AndroidViewModel(application) {
//    // упрощённый вариант
//    private val repository: PostRepository = PostRepositoryImpl()
//    private val _data = MutableLiveData(FeedModel())
//    val data: LiveData<FeedModel>
//        get() = _data
//    val edited = MutableLiveData(empty)
//    private val _postCreated = SingleLiveEvent<Unit>()
//    val postCreated: LiveData<Unit>
//        get() = _postCreated
//
//
//    init {
//        loadPosts()
//    }
//
//    fun loadPosts() {
//        _data.value = FeedModel(loading = true)
//        repository.getAllAsync(object : PostRepository.GetAllCallback {
//            override fun onSuccess(posts: List<Post>) {
//                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//            }
//
//            override fun onError(e: Exception) {
//                _data.postValue(FeedModel(error = true))
//            }
//        })
//    }
//
//    fun refresh() {
//        _data.value = FeedModel(refreshing = true)
//        repository.getAllAsync(object : PostRepository.GetAllCallback {
//            override fun onSuccess(posts: List<Post>) {
//                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//
//            }
//
//            override fun onError(e: Exception) {
//                _data.postValue(FeedModel(error = true))
//            }
//        })
//    }
//
//    fun save() {
//        edited.value?.let {
//            repository.saveAsync(it, object : PostRepository.SaveCallback {
//                override fun onSuccess(posts: Post) {
//                    _postCreated.postValue(Unit)
//                    edited.postValue(empty)
//                }
//
//                override fun onError(e: Exception) {
//                    edited.postValue(empty)
//                }
//            })
//        }
//    }
//
//    fun changeContent(content: String) {
//        val text = content.trim()
//        if (edited.value?.content == text) {
//            return
//        }
//        edited.value = edited.value?.copy(content = text)
//    }
//
//    fun edit(post: Post) {
//        edited.value = post
//    }
//
//    fun removeById(id: Long) {
//
//        repository.removeByIdAsync(id, object : PostRepository.removeByIdCallback {
//            val old = _data.value?.posts.orEmpty()
//            override fun onSuccess(id: Long) {
//
//                _data.postValue(
//                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                        .filter { it.id != id }
//                    )
//                )
//            }
//
//            override fun onError(e: Exception) {
//                _data.postValue(_data.value?.copy(posts = old))
//
//            }
//        })
//    }
//
//    fun likeById(id: Long) {
//        repository.likeByIdSync(id, object : PostRepository.likeByIdCallback {
//            override fun onSuccess(post: Post) {
//                _data.postValue(
//                    FeedModel(
//                        posts = _data.value?.posts.orEmpty().map {
//                            if (it.id == post.id) post else it
//                        }
//                    )
//                )
//            }
//            override fun onError(e: Exception) {
//                _data.postValue(FeedModel(error = true))
//            }
//        })
//    }
//
//    fun unlikeById(id: Long) {
//        repository.unlikeByIdAsync(id, object : PostRepository.unlikeByCallback {
//            override fun onSuccess(post: Post) {
//
//                _data.postValue(
//                    FeedModel(
//                        posts = _data.value?.posts.orEmpty().map {
//                            if (it.id == post.id) post else it
//                        }
//                    )
//                )
//            }
//            override fun onError(e: Exception) {
//                _data.postValue(FeedModel(error = true))
//            }
//        })
//    }
//}
