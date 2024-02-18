package KlepetChat.WebApi.Models.Response

import java.util.UUID

data class User (
    val id: UUID,
    val surname: String,
    val name: String,
    val nickname: String?,
    val photo: String?,
    val aboutMe: String?,
)