package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun getAllAsync(callback: Callback<List<Post>>)
    fun saveAsync(post: Post, callback: Callback<Post>)
    fun unlikeByIdAsync (id: Long, callback: Callback<Post>)
    fun likeByIdASync (id: Long, callback: Callback<Post>)
    fun removeByIdAsync(id: Long, callback: Callback<Unit>)


    interface Callback<T> {
        fun onSuccess(posts: T) {}
        fun onError(e: Exception) {}
    }


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