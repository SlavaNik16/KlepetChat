package KlepetChat.WebApi.Models.Request

import com.google.gson.annotations.SerializedName

data class Auth (
    @SerializedName("Phone")
    val Phone: String,
    @SerializedName("Password")
    val Password: String
)