package KlepetChat.WebApi.Implementations

import KlepetChat.WebApi.Models.Exceptions.Error
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response

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

