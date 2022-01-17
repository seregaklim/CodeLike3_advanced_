package ru.netology.nmedia.repository

import okio.IOException
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError

class AuthRepositoryimpl():AuthRepository {



    override  suspend fun  loginUser( login: String,  pass: String) {
      try {
          val response = Api.service. loginUser(login,pass)
          if (!response.isSuccessful) {
              throw ApiError(response.code(), response.message())
          }

      } catch (e: IOException) {
          throw NetworkError
      } catch (e: Exception) {
          throw UnknownError
      }


    }

   override suspend fun registerUser ( login: String, pass: String, name: String) {

       try {
           val response = Api.service.registerUser(login,pass,name)
           if (!response.isSuccessful) {
               throw ApiError(response.code(), response.message())
           }

       } catch (e: IOException) {
           throw NetworkError
       } catch (e: Exception) {
           throw UnknownError
       }
   }
}

