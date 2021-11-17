package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    author ="",
    content = "",
    likedByMe = false,
    likes = 0,
    published = "",
    authorAvatar ="",
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
            repository.saveAsync(it, object : PostRepository.SaveCallback {
                override fun onSuccess(posts: Post) {
                    _postCreated.postValue(Unit)
                    edited.postValue(empty)
                }

                override fun onError(e: Exception) {
                    edited.postValue(empty)
                }
            })
        }
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun removeById(id: Long) {

            repository.removeByIdAsync(id, object : PostRepository.removeByIdCallback {
                val old = _data.value?.posts.orEmpty()
                override fun onSuccess(id: Long) {

                    _data.postValue(
                        _data.value?.copy(posts = _data.value?.posts.orEmpty()
                            .filter { it.id != id }
                        )
                   )
                }

                override fun onError(e: Exception) {
                  _data.postValue(_data.value?.copy(posts = old))

                }
            })
    }


        fun likeById(id: Long) {

                repository.likeByIdSync(id, object : PostRepository.likeByIdCallback {
                    override fun onSuccess(id: Long) {

                        val liked = repository.likeById(id)
                        _data.postValue(
                            FeedModel(
                                posts = _data.value?.posts.orEmpty().map {
                                    if (it.id == liked.id) liked else it
                                }
                            )
                        )
                    }
                    override fun onError(e: Exception) {
                        _data.postValue(FeedModel(error = true))
                    }
                })
        }

    fun unlikeById(id: Long) {
            repository.unlikeByIdAsync(id, object : PostRepository.unlikeByCallback {
                override fun onSuccess(id: Long) {

                    val unliked= repository.unlikeById(id)
                    _data.postValue(
                        FeedModel(
                            posts = _data.value?.posts.orEmpty().map {
                                if (it.id == unliked.id) unliked else it
                            }
                        )
                    )
                }
                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
    }
}
