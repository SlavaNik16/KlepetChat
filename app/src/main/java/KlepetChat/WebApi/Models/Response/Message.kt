package KlepetChat.WebApi.Models.Response

import com.google.gson.annotations.SerializedName
import java.util.Date
import java.util.UUID

data class Message (
    @SerializedName("id")
    val id: UUID,
    @SerializedName("name")
    val name:String,
    @SerializedName("text")
    val text: String,
    @SerializedName("createdAt")
    val createdAt: Date,
    @SerializedName("chat")
    val chat: Chat,

)