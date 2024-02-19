package KlepetChat.WebApi.Implementations

/**
 * Этот клас работает с запросами,
 * что поможет нам извлекать данные
 * @param T - тип
 */
sealed class ApiResponse<out T>{
    data object Loading : ApiResponse<Nothing>()

    data class Success<out T>(
        val data: T,
    ) : ApiResponse<T>()

    data class Failure(
        val code: Int,
        val message: String,
    ) : ApiResponse<Nothing>()
}

