package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okio.IOException
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dao.AuthStateDao
import ru.netology.nmedia.dao.UserDao
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.entity.*
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError


class AuthRepositoryimpl(private val daoAuthStateDao: AuthStateDao,private val daoUser:UserDao):AuthRepository {


    override  suspend fun updateUser( login: String,  pass: String) {
      try {
          val response = ru.netology.nmedia.api.Api.service.updateUser(login,pass)
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
         //  daoUser.registerUser(login,pass,name)

       } catch (e: IOException) {
           throw NetworkError
       } catch (e: Exception) {
           throw UnknownError
       }

   }

     suspend fun getUserId(id:Long, token: String) {
         try {
             val response = ru.netology.nmedia.api.Api.service.getUserId(id, token)
             if (!response.isSuccessful) {
                 throw ApiError(response.code(), response.message())
             }
             daoAuthStateDao.getUserId(id, token)

         } catch (e: IOException) {
             throw NetworkError
         } catch (e: Exception) {
             throw UnknownError
         }

    }
}

