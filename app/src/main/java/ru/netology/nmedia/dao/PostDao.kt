package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.enumeration.AttachmentType

@Dao
interface PostDao {



    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    suspend fun likeById(id: Long)

    @Query(
        """
        UPDATE PostEntity SET
        newer = newer ++1
        
        """
    )
    suspend fun countMessegePost()


    @Query(
        """
        UPDATE PostEntity SET
        newer = newer - newer
        
        """
    )
    suspend fun unCountNewer()




}

class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
}








//@Dao
//interface PostDao {
//    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
//    fun getAll(): LiveData<List<PostEntity>>
//
//    @Insert
//    fun insert(post: PostEntity)
//
//    @Insert
//    fun insert(posts: List<PostEntity>)
//
//    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
//    fun updateContentById(id: Long, content: String)
//
//    fun save(post: PostEntity) =
//        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

//    @Query("""
//        UPDATE PostEntity SET
//        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
//        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
//        WHERE id = :id
//        """)
//    fun likeById(id: Long)

//    @Query("DELETE FROM PostEntity WHERE id = :id")
//    fun removeById(id: Long)
//}