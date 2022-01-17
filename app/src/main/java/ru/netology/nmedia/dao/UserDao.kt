package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.AuthStateEntity
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity ORDER BY id DESC")
    suspend fun  registerUser ( login: String, name: String)

    @Query("SELECT COUNT(*) == 0 FROM  UserEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user:UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: List< UserEntity>)


//    @Query(
//        """
//        UPDATE UserEntity SET
//        login = login +
//        name = name
//        """
//    )
//   suspend fun  registerUser ( login: String, pass: String, name: String)
}