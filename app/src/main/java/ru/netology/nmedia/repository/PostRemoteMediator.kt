package ru.netology.nmedia.repository
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val service: ApiService,
    private val db: AppDb,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            //В зависимости от типа загрузки (LoadType) мы выбираем нужный запрос
            // раньше/позже какого id грузить,это определяется из state:
            //Где Success позволяет указать, нужно ли далее запрашивать данные (или они «закончились»):
            val response = when (loadType) {

                LoadType.REFRESH -> {
                    //    данные сверху добавлялись, учитывая id,
                    // нужно использовать getAfter (если ключ есть , иначе getLatest)
                    val id = postRemoteKeyDao.max()
                    if (id == null) {
                        service.getLatest(state.config.initialLoadSize)
                    }else{   postRemoteKeyDao.max() ?: service.getLatest(state.config.initialLoadSize)
                        service.getAfter(id , state.config.pageSize)

                    }}

                //забираем самый верхний элемент из базы
                //В данный момент, Автоматический PREPEND был отключен
                // (т.е. при scroll'е к первому сверху элементу данные автоматически не подгружались).
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

                //забираем самый нижний элемент из базы
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message(),
            )
            //если прийдет пустой список
            if(body.isEmpty()){
                return  MediatorResult.Success(false)
            }

            db.withTransaction {
                //при первой загрузке запускается REFRESH, в случае успеха удаляем старый кеш постов из БД;
                when (loadType) {
                    LoadType.REFRESH -> {
                        //REFRESH не затирал предыдущий кеш, а добавлял данные сверху,
                        //учитывая id последнего поста сверху (соответственно, swipe to refresh должен
                        // "добавлять" данные, а не затирать их).

                        // postRemoteKeyDao.removeAll()
                        postRemoteKeyDao.insert(
                            listOfNotNull(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.AFTER,
                                    //первый элемент
                                    id = body.first().id,
                                ),
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.BEFORE,
                                    //последний элемент
                                    id = body.last().id,
                                ).takeIf { postRemoteKeyDao.isEmpty() },
                            )
                        )

                        // postDao.removeAll()
                    }
                    //далее  PREPEND/APPEND
                    LoadType.PREPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.AFTER,
                                id = body.first().id,
                            )
                        )
                    }
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                id = body.last().id,
                            )
                        )
                    }
                }
                //при swipe-to-refresh снова REFRESH с последующими PREPEND/APPEND
                postDao.insert(body.toEntity())
            }

            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}


























//
//package ru.netology.nmedia.repository
//
//import androidx.paging.ExperimentalPagingApi
//import androidx.paging.LoadType
//import androidx.paging.PagingState
//import androidx.paging.RemoteMediator
//import androidx.room.withTransaction
//import ru.netology.nmedia.api.ApiService
//import ru.netology.nmedia.dao.PostDao
//import ru.netology.nmedia.dao.PostRemoteKeyDao
//import ru.netology.nmedia.db.AppDb
//import ru.netology.nmedia.entity.PostEntity
//import ru.netology.nmedia.entity.PostRemoteKeyEntity
//import ru.netology.nmedia.entity.toEntity
//import ru.netology.nmedia.error.ApiError
//import javax.inject.Inject
//
//@OptIn(ExperimentalPagingApi::class)
//class PostRemoteMediator @Inject constructor(
//    private val service: ApiService,
//    private val db: AppDb,
//    private val postDao: PostDao,
//    private val postRemoteKeyDao: PostRemoteKeyDao,
//) : RemoteMediator<Int, PostEntity>() {
//
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, PostEntity>
//    ): MediatorResult {
//        try {
//            //В зависимости от типа загрузки (LoadType) мы выбираем нужный запрос
//            // раньше/позже какого id грузить,это определяется из state:
//            //Где Success позволяет указать, нужно ли далее запрашивать данные (или они «закончились»):
//            val response = when (loadType) {
//
//                //размер страницы
//                LoadType.REFRESH -> service.getLatest(state.config.initialLoadSize)
//
//                //забираем самый верхний элемент из базы
//                LoadType.PREPEND -> {
//                    val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(
//                        endOfPaginationReached = false
//                    )
//                    service.getAfter(id, state.config.pageSize)
//                }
//                //забираем самый нижний элемент из базы
//                LoadType.APPEND -> {
//                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(
//                        endOfPaginationReached = false
//                    )
//                    service.getBefore(id, state.config.pageSize)
//                }
//            }
//
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//            val body = response.body() ?: throw ApiError(
//                response.code(),
//                response.message(),
//            )
//            //если прийдет пустой список
//            if(body.isEmpty()){
//                return  MediatorResult.Success(false)
//            }
//
//            db.withTransaction {
//                //при первой загрузке запускается REFRESH, в случае успеха удаляем старый кеш постов из БД;
//                when (loadType) {
//                    LoadType.REFRESH -> {
//                        postRemoteKeyDao.removeAll()
//                        postRemoteKeyDao.insert(
//                            listOf(
//                                PostRemoteKeyEntity(
//                                    type = PostRemoteKeyEntity.KeyType.AFTER,
//                                    //первый элемент
//                                    id = body.first().id,
//                                ),
//                                PostRemoteKeyEntity(
//                                    type = PostRemoteKeyEntity.KeyType.BEFORE,
//                                    //последний элемент
//                                    id = body.last().id,
//                                ),
//                            )
//                        )
//                        postDao.removeAll()
//                    }
//                    //далее  PREPEND/APPEND
//                    LoadType.PREPEND -> {
//                        postRemoteKeyDao.insert(
//                            PostRemoteKeyEntity(
//                                type = PostRemoteKeyEntity.KeyType.AFTER,
//                                id = body.first().id,
//                            )
//                        )
//                    }
//                    LoadType.APPEND -> {
//                        postRemoteKeyDao.insert(
//                            PostRemoteKeyEntity(
//                                type = PostRemoteKeyEntity.KeyType.BEFORE,
//                                id = body.last().id,
//                            )
//                        )
//                    }
//                }
//                //при swipe-to-refresh снова REFRESH с последующими PREPEND/APPEND
//                postDao.insert(body.toEntity())
//            }
//
//            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
//        } catch (e: Exception) {
//            return MediatorResult.Error(e)
//        }
//    }
//}






