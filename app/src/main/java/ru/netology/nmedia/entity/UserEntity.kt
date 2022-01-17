package ru.netology.nmedia.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.User

@Entity
data class UserEntity(
   @PrimaryKey(autoGenerate = true)
   val id: Long,
   val login: String,
   val name: String,
   val avatar: String,
   val authorities: List<String>,
   )
{
   fun toDto() = User(    id, login, name, avatar, authorities )

   companion object {
      fun fromDto(dto: User) = UserEntity(dto.id, dto.login, dto.name, dto.avatar, dto.authorities)
   }
}
fun List< UserEntity>.toDto(): List< User> = map( UserEntity::toDto)
fun List< User>.toEntity(): List< UserEntity> = map( UserEntity::fromDto)


