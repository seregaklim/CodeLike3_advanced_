package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post


import kotlinx.coroutines.flow.Flow
import retrofit2.http.Field
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.User


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