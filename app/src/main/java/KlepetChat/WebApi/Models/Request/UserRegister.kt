package KlepetChat.WebApi.Models.Request

data class UserRegister (
    val surname: String,
    val name: String,
    val phone: String,
    val password: String,
)