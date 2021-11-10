package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun refresh() {
    _data.value = FeedModel(refreshing = true)
    repository.getAllAsync(object : PostRepository.GetAllCallback {
        override fun onSuccess(posts: List<Post>) {
            _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
        }

        override fun onError(e: Exception) {
            _data.postValue(FeedModel(error = true))
        }
    })
}

    fun save() {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
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

    fun removeById(id: Long) {
        thread {
            // Оптимистичная модель
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

    fun likeById(id: Long) {
        thread {
            try {
                val liked = repository.likeById(id)
                _data.postValue(
                    FeedModel(
                        posts = _data.value?.posts.orEmpty().map {
                            if (it.id == liked.id) liked else it
                        }
                    )
                )
            } catch (e: IOException) {

                // TODO Обработка ошибки
            }
        }
    }

    fun unlikeById(id: Long) {
        thread {
            try {
                val unliked = repository.unlikeById(id)
                _data.postValue(
                    FeedModel(
                        posts = _data.value?.posts.orEmpty().map {
                            if (it.id == unliked.id) unliked else it
                        }
                    )
                )
            } catch (e: IOException) {
                // TODO Обработка ошибки
            }
        }
    }
}


//        fun refresh() {
//        thread {
//// Начинаем загрузку
//            _data.postValue(FeedModel(refreshing = true)) // <- вот здесь другой флаг
//            try {
//// Данные успешно получены
//                val posts = repository.getAll()
//                FeedModel(posts = posts, empty = posts.isEmpty())
//            } catch (e: IOException) {
//// Получена ошибка
//                FeedModel(error = true)
//            }.also(_data::postValue)
//        }
//    }