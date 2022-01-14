package ru.netology.nmedia.repository

import retrofit2.http.Field
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.User

interface  AuthRepository{

    suspend fun updateUser(@Field("login") login: String, @Field("pass") pass: String): User

    suspend fun registerUser (@Field("login") login: String,
                              @Field("pass") pass: String,
                              @Field("name") name: String): User


    suspend fun getUserId(login: String, password: String)
}