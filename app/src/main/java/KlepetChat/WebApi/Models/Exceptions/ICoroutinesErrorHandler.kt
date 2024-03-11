package KlepetChat.WebApi.Models.Exceptions

interface ICoroutinesErrorHandler {
    fun onError(message: String)
}