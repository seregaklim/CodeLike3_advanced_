package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Timing
import java.time.LocalDate

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val newer:Long,
    val  authorId: Long,
    var ownedByMe: Boolean = false,
    var  timing: Long,
    @Embedded
    var attachment: AttachmentEmbeddable?,
) {
    fun toDto() = Post(
        id,
        author,
        authorAvatar,
        content,
        published,
        likedByMe,
        likes,
        newer,
        authorId,
        ownedByMe,
         timing,
        attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes,dto.newer,
                dto.authorId,dto.ownedByMe,dto.timing, AttachmentEmbeddable.fromDto(dto.attachment))

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)