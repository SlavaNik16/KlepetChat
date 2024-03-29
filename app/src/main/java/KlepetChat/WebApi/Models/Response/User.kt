package KlepetChat.WebApi.Models.Response

import KlepetChat.WebApi.Models.Response.Enums.StatusTypes
import java.util.UUID

data class User(
    val id: UUID,
    val surname: String,
    val name: String,
    val phone: String,
    val nickName: String?,
    var photo: String?,
    val aboutMe: String?,
    val status: StatusTypes = StatusTypes.Offline,
)