package KlepetChat.WebApi.Models.Request

import com.google.gson.annotations.SerializedName

data class FIO (
    @SerializedName("surname")
    val surname:String,
    @SerializedName("name")
    val name:String,
)