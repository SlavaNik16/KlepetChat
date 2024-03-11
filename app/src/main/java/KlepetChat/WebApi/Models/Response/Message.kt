package KlepetChat.WebApi.Models.Response

import com.google.gson.annotations.SerializedName
import java.util.Date
import java.util.UUID

data class Message(
    @SerializedName("id")
    val id: UUID,
    @SerializedName("nameBy")
    val nameBy: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("text")
    val text: String,
    @SerializedName("createdAt")
    val createdAt: Date,
    @SerializedName("chatId")
    val chatId: UUID,
)