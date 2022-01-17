package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.User

interface  AuthRepository {

    suspend fun updateUser(login: String, pass: String)

    suspend fun registerUser(login: String, pass: String, name: String)

//    suspend fun getUserId(id: Long, token: String)
}