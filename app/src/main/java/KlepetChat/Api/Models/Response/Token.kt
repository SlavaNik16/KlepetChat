package KlepetChat.Api.Models.Response

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("AccessToken")
    val accessToken: String,
    @SerializedName("RefreshToken")
    val refreshToken: String,
)