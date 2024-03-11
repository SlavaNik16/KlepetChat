package KlepetChat.DataSore.Models

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("phone")
    val phone: String,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
)