package ru.netology.nmedia.api

import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

fun okhttp(vararg interceptors: Interceptor): OkHttpClient = OkHttpClient.Builder()
    .apply {
        interceptors.forEach {
            this.addInterceptor(it)
        }
    }
    .build()

fun retrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface ApiService {

    @POST("users/push-tokens")
    suspend fun save(@Body pushToken: PushToken): Response<Unit>


    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun unlikeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

    //метод  который возвращает все посты новее определённого:
    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    //Примечание*: в реальной жизни никто вам не даст загружать сколько угодно,
    // т.к. это брешь в безопасности. Всегда узнавайте у разработчика API лимит.

    //Дает возможность загружать записи, «старше»  (т.е. созданные раньше) по id указанной:
    //Для подгрузки данных при скроллировании вниз:
    @GET("posts/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>


    //Дает возможность загружать последние 10 (или сколько указано пользователем записей*)
   //Для загрузки начальных данных (в определенном количестве)
    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>


    @GET("posts/{id}/after")
    suspend fun getAfter(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>


    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun  loginUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<Token>


    @FormUrlEncoded
   @POST("users/registration")
    suspend fun registerUser(@Field("login") login: String,
                             @Field("name") name: String,
                             @Field("pass") pass: String, ): Response<Token>

}
//object Api {
//    val service: ApiService by lazy {
//        retrofit.create(ApiService::class.java)
//    }
//}
