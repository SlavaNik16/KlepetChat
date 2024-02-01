package KlepetChat.WebApi.Models.Request

import com.google.gson.annotations.SerializedName

data class Login (
    @SerializedName("Phone")
    val phone: String,
    @SerializedName("Password")
    val password: String
)