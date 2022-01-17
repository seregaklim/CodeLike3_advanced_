package ru.netology.nmedia.repository

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import okio.IOException
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.entity.*
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError


class AuthRepositoryimpl():AuthRepository {



    override  suspend fun  loginUser( login: String,  pass: String) {
      try {
          val response = ru.netology.nmedia.api.Api.service. loginUser(login,pass)
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
           val response = ru.netology.nmedia.api.Api.service.registerUser(login,pass,name)
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

