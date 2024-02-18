package KlepetChat.WebApi.Models.Exceptions

import com.google.gson.annotations.SerializedName

data class InvalidateItemModel (
    @SerializedName("field")
    val field:String,
    @SerializedName("message")
    val message:String,
)