package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.AuthStateEntity
import ru.netology.nmedia.entity.PostEntity

@Dao
interface AuthStateDao{
    @Query("SELECT * FROM AuthStateEntity ORDER BY id DESC")
    suspend fun getUserId (id: Long, token: String)


    @Query("SELECT COUNT(*) == 0 FROM AuthStateEntity")
    //suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(authState: AuthStateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(authState: List<AuthStateEntity>)

//    @Query(
//        """
//        UPDATE AuthStateEntity SET
//        id = id +
//        token = token
//
//
//        """
//    )
//    suspend fun getUserId (id: Long, token: String)

}

