package KlepetChat.WebApi.Models.Response.Enums

import com.google.gson.annotations.SerializedName

enum class ChatTypes(val value:Int) {
    @SerializedName("0")
    Group(0),
    @SerializedName("1")
    Contact(1),
    @SerializedName("2")
    Favorites(2),
}