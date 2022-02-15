package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity

@Dao
interface PostRemoteKeyDao {
    //подписаться на конкретный пост в PostDao (лайк)
    @Query("SELECT * FROM PostEntity WHERE id = :id")
    fun getById(id: Long): Flow<PostEntity?>


    @Query("SELECT COUNT(*) == 0 FROM PostRemoteKeyEntity")
    suspend fun isEmpty(): Boolean

    //запись с максимальным id
    @Query("SELECT MAX(id) FROM PostRemoteKeyEntity")
    suspend fun max(): Long?

    //запись с минимальным id
    @Query("SELECT MIN(id) FROM PostRemoteKeyEntity")
    suspend fun min(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: PostRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<PostRemoteKeyEntity>)

    // позволит нам удалить все посты (будем удалять их при Refresh'е):
    @Query("DELETE FROM PostRemoteKeyEntity")
    suspend fun removeAll()

    //подписаться на id первого поста в БД
    @Query("SELECT MAX(id) FROM PostRemoteKeyEntity")
    fun getFirstPostId(): Flow<Long?>
}