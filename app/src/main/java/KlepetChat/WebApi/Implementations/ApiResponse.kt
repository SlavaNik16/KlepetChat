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
    object Loading : ApiResponse<Nothing>()

    data class Success<out T>(
        val data: T,
    ) : ApiResponse<T>()

    data class Failure(
        val code: Int,
        val message: String,
    ) : ApiResponse<Nothing>()

    /**
     * Функция выполняет вызовы API,
     * в потоке ввода/ввывода,
     * @param flow - создание асинхронного потока в функции flow
     * @param withTimeoutOrNull - Тайм-аут(20 сек) остановки
     * @param emit - имитируется получение объектов из бд
     */
    fun<T> ApiRequestFlow(call: suspend () -> Response<T>): Flow<ApiResponse<T>> = flow {
        emit(Loading)

        withTimeoutOrNull(20000L){
            val response = call()

            try{
                if(response.isSuccessful){
                    response.body()?.let {
                            data-> emit(Success(data))
                    }
                }else{
                    response.errorBody()?.let { error ->
                        error.close()
                        val parsedError: Error =
                            Gson().fromJson(error.charStream(), Error::class.java)
                        emit(Failure(parsedError.code ,parsedError.message))
                    }
                }
            }catch (e:Exception){
                emit(Failure(400, e.message ?: e.toString()))
            }
        } ?:  emit(Failure(408,"Время вышло! Пожалуйста повторите попытку снова."))
    }.flowOn(Dispatchers.IO)
}

