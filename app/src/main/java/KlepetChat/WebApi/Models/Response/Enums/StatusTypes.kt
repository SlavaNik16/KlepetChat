package KlepetChat.WebApi.Models.Response.Enums

import com.google.gson.annotations.SerializedName

enum class StatusTypes(val value: Int) {
    @SerializedName("0")
    Offline(0),

    @SerializedName("1")
    Online(1),
}