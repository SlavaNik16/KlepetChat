package KlepetChat.WebApi.Models.Response

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("accessToken")
    val accessToken: String?,
    @SerializedName("refreshToken")
    val refreshToken: String?,
)