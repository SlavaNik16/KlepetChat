package KlepetChat.WebApi.Models.Request

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("phone")
    val phone: String,
    @SerializedName("password")
    val password: String,
)