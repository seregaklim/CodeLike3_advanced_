package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post


import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload


interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAll()
    //подписаться на изменения data
    fun getNewerCount(id: Long):  Flow<Int>

    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun  unlikeById (id: Long)

    suspend fun countMessegePost()
    suspend fun  unCountNewer()

    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
}

































//package ru.netology.nmedia.repository
//
//import ru.netology.nmedia.dto.Post
//
//interface PostRepository {
//    fun saveAsync(post: Post, callback: SaveCallback)
//    fun removeByIdAsync(id: Long, callback: removeByIdCallback)
//    fun unlikeByIdAsync(id: Long, callback: unlikeByCallback)
//    fun likeByIdSync(id: Long, callback: likeByIdCallback)
//    fun getAllAsync(callback: GetAllCallback)
//
//    interface GetAllCallback {
//        fun onSuccess(posts: List<Post>) {}
//        fun onError(e: Exception) {}
//    }
//
//    interface SaveCallback {
//        fun onSuccess(posts: Post) {}
//        fun onError(e: Exception) {}
//    }
//
//    interface removeByIdCallback {
//        fun onSuccess(id: Long) {}
//        fun onError(e: Exception) {}
//    }
//
//    interface unlikeByCallback {
//        fun onSuccess(post: Post) {}
//        fun onError(e: Exception) {}
//    }
//
//    interface likeByIdCallback {
//        fun onSuccess(post: Post) {}
//        fun onError(e: Exception) {}
//    }
//
//
//}