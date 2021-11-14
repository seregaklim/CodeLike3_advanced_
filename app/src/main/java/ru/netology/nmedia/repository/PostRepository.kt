package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long): Post
    fun save(post: Post)
    fun removeById(id:Long)
    fun unlikeById(id: Long):Post

    fun  saveAsync(callback: GetAllCallback)
    fun  removeByIdAsync(callback: GetAllCallback)
    fun  unlikeByIdAsync(callback: GetAllCallback)
    fun likeByIdSync (callback: GetAllCallback)
    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }
}