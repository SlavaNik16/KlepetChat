package KlepetChat.WebApi.Models.Response

import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Chat (
    @SerializedName("id")
    val id: UUID,
    @SerializedName("name")
    val name: String,
    @SerializedName("lastMessage")
    val lastMessage: String?,
    @SerializedName("chatType")
    val chatType: ChatTypes,
    @SerializedName("photo")
    val photo: String
)