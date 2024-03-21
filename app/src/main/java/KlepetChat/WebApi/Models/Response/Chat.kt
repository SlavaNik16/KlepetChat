package KlepetChat.WebApi.Models.Response

import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import KlepetChat.WebApi.Models.Response.Enums.RoleTypes
import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Chat(
    @SerializedName("id")
    val id: UUID,
    @SerializedName("name")
    val name: String,
    @SerializedName("lastMessage")
    val lastMessage: String?,
    @SerializedName("phones")
    val phones: MutableList<String>,
    @SerializedName("chatType")
    val chatType: ChatTypes,
    @SerializedName("roleType")
    val roleType: RoleTypes,
    @SerializedName("photo")
    val photo: String,
)