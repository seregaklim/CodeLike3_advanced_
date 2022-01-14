package ru.netology.nmedia.repository

import retrofit2.http.Field
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError

class AuthRepositoryimpl:  AuthRepository {

    override suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): User {
        try {
            val response = Api.service.updateUser("login", "pass")
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun registerUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): User {

        try {
            val response = Api.service.registerUser(
                "",
                "", ""
            )

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getUserId(login: String, password: String) {

        try {
            val response = Api.service.getUserId( "id" ,
               "token")


            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())

        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}