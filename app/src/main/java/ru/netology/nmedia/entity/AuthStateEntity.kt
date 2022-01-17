package ru.netology.nmedia.entity

import androidx.constraintlayout.compose.DesignElements.map
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.User

@Entity
data class AuthStateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    var token: String? = null

) {
    fun toDto() = AuthState(id, token)

    companion object {
        fun fromDto(dto: AuthState) = AuthStateEntity(dto.id, dto.token)
    }
}
fun List< AuthStateEntity>.toDto(): List< AuthState> = map(AuthStateEntity::toDto)
fun List< AuthState>.toEntity(): List< AuthStateEntity> = map(AuthStateEntity::fromDto)