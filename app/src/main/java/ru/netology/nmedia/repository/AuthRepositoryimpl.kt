package ru.netology.nmedia.repository

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import retrofit2.http.Field
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.viewmodel.authState
import ru.netology.nmedia.viewmodel.token


class AuthRepositoryimpl():AuthRepository {


    override  suspend fun  loginUser( login: String,  pass: String):Token {
        try {

                authState.copy(id = token.id, token=token.token )

            val response = Api.service. loginUser(login,pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            //возвращает ответ
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

   override suspend fun registerUser ( login: String, pass: String, name: String):Token {
    try {

           authState.copy(id = token.id, token=token.token )

           val response = Api.service.registerUser(login,pass,name)
           if (!response.isSuccessful) {
               throw ApiError(response.code(), response.message())
           }
          //возвращает ответ
           return response.body() ?: throw ApiError(response.code(), response.message())
       } catch (e: IOException) {
           throw NetworkError
       } catch (e: Exception) {
           throw UnknownError
       }
   }
}

