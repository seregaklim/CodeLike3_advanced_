package ru.netology.nmedia.entity

import androidx.room.Entity
import ru.netology.nmedia.dto.NewMessegePost
import ru.netology.nmedia.dto.Post

@Entity
data class NewMessegePostEntity(
    val newer:Long,
    val post: Post
) {
    fun toDto() = NewMessegePost(newer, post)

    companion object {
        fun fromDto(dto: NewMessegePost) =
            NewMessegePostEntity(dto.newer, dto.post)
    }

}


fun List<NewMessegePostEntity>.toDto(): List<NewMessegePost> = map(NewMessegePostEntity::toDto)
fun List<NewMessegePost>.toEntity(): List<NewMessegePostEntity> =
    map(NewMessegePostEntity::fromDto)