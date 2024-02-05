package KlepetChat.WebApi.Models.Response

import com.google.gson.annotations.SerializedName

data class Token(
    val accessToken: String?,
    val refreshToken: String?,
)