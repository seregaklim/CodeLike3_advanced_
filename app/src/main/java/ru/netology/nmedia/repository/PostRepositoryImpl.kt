package ru.netology.nmedia.repository

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
) : PostRepository {

    override val data = postDao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity())
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    //метод  который возвращает все посты новее определённого:
    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(120_000L)
            val response = apiService.getNewer(id)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            //не нужен, обнуляет newer
            postDao.insert(body.toEntity())
            emit(body.size)
        }

    }//можем использовать catch в нашем flow и избавиться от отдельной обработки
        // CancellationException, так как catch уже знает о нём
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)


    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            postDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            val response = apiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.likeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun unlikeById(id: Long) {
        try {
            val response = apiService.unlikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            //код одинаковый с лайк, коды лайк и дизлайк объединены , так как запрос в sql один
            postDao.likeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun countMessegePost() {
        try {
            postDao.countMessegePost()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun unCountNewer() {
        try {
            postDao.unCountNewer()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            // TODO: add support for other types
            val postWithAttachment =
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = apiService.upload(media)
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

}

















//class AuthRepositoryimpl:  AuthRepository ( val dao: AuthStateDao)
//{

//    override suspend fun updateUser(
//        @Field("login") login: String,
//        @Field("pass") pass: String
//    ): User {
//        try {
//            val response = Api.service.updateUser("login", "pass")
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            return response.body() ?: throw ApiError(response.code(), response.message())
//        } catch (e: java.io.IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
//    }
//
//
//    override suspend fun registerUser(
//        @Field("login") login: String,
//        @Field("pass") pass: String,
//        @Field("name") name: String
//    ): User {
//
//        try {
//            val response = Api.service.registerUser(
//                "",
//                "", ""
//            )
//
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            return response.body() ?: throw ApiError(response.code(), response.message())
//        } catch (e: java.io.IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
//    }
//
//    override suspend fun getUserId(login: String, password: String) {
//
//        try {
//            val response = Api.service.getUserId( "id" ,
//                "token")
//
//
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//
//        } catch (e: java.io.IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
//    }























//class PostRepositoryImpl : PostRepository {
//
//    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
//        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
//            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException(response.message()))
//                    return
//                }
//
//                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//            }
//
//            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
//              TODO("Not yet implemented")
//
//            }
//        })
//    }
//
//    override fun saveAsync(post: Post, callback: PostRepository.Callback<Post>) {
//
//        PostsApi.retrofitService.save(post).enqueue(object : Callback<Post> {
//            override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException(response.message()))
//                    return
//                }
//
//                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//            }
//
//            override fun onFailure(call: Call<Post>, t: Throwable) {
//               TODO("Not yet implemented")
//
//            }
//        })
//    }
//
//    override fun removeByIdAsync(id: Long, callback: PostRepository.Callback<Unit>) {
//        PostsApi.retrofitService.removeById(id).enqueue(object : Callback<Unit> {
//            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException(response.message()))
//                    return
//                }
//
//                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//            }
//
//            override fun onFailure(call: Call<Unit>, t: Throwable) {
//               TODO("Not yet implemented")
//
//            }
//        })
//    }
//
//    override fun likeByIdASync(id: Long, callback: PostRepository.Callback<Post>) {
//        PostsApi.retrofitService.likeById(id).enqueue(object : Callback<Post> {
//            override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException(response.message()))
//                    return
//                }
//                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//
//            }
//
//            override fun onFailure(call: Call<Post>, t: Throwable) {
//                TODO("Not yet implemented")
//
//
//            }
//        })
//    }
//
//    override fun unlikeByIdAsync(id: Long, callback: PostRepository.Callback<Post>) {
//        PostsApi.retrofitService.unlikeById(id).enqueue(object : Callback<Post> {
//            override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException(response.message()))
//                    return
//                }
//
//                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//            }
//
//            override fun onFailure(call: Call<Post>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//        })
//
//
//    }
//}




