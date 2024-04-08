package KlepetChat.WebApi.Implementations

import KlepetChat.WebApi.Models.Exceptions.ApiValidationExceptionDetail
import KlepetChat.WebApi.Models.Exceptions.Error
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response

/**
 * Функция выполняет вызовы API,
 * в потоке ввода/ввывода,
 * @param flow - создание асинхронного потока в функции flow
 * @param withTimeoutOrNull - Тайм-аут(20 сек) остановки
 * @param emit - имитируется получение объектов из бд
 */
fun <T> ApiRequestFlowResponse(call: suspend () -> Response<T>): Flow<ApiResponse<T>> = flow {
    emit(ApiResponse.Loading)

    withTimeoutOrNull(20000L) {
        val response = call()

        try {
            Log.d("POST", "ResponseTry: ${response.isSuccessful}")
            if (response.isSuccessful) {
                Log.d("POST", "Response: ${response.body()}")
                response.body()?.let { data ->
                    emit(ApiResponse.Success(data))
                }
            } else {
                response.errorBody()?.let { error ->
                    error.close()
                    when (response.code()) {
                        409 -> {
                            val invalidate = Gson().fromJson(
                                error.charStream(),
                                ApiValidationExceptionDetail::class.java
                            )
                            for (index: Int in 0 until invalidate.errors.size) {
                                emit(ApiResponse.Failure(409, invalidate.errors[index].message))
                            }
                        }

                        else -> {
                            val parsedError: Error = Gson().fromJson(
                                error.charStream(),
                                Error::class.java
                            )
                            emit(ApiResponse.Failure(parsedError.code, parsedError.message))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(ApiResponse.Failure(400, e.message ?: e.toString()))
        }
    } ?: emit(ApiResponse.Failure(408, "Время вышло! Пожалуйста повторите попытку снова."))
}.flowOn(Dispatchers.IO)

