package KlepetChat.WebApi.Models.Response.Enums

import com.google.gson.annotations.SerializedName

enum class RoleTypes(val value: Int) {
    @SerializedName("0")
    User(0),

    @SerializedName("1")
    Admin(1),
}