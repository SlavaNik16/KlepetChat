package KlepetChat.Api.Models.Request

import com.google.gson.annotations.SerializedName

data class Auth (
    @SerializedName("Phone")
    val phone: String,
    @SerializedName("Password")
    val password: String
)