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
     * Функция выполняет вызовы API,
     * в потоке ввода/ввывода,
     * @param flow - создание асинхронного потока в функции flow
     * @param withTimeoutOrNull - Тайм-аут(20 сек) остановки
     * @param emit - имитируется получение объектов из бд
     */
    fun<T> ApiRequestFlow(call: suspend () -> Response<T>): Flow<ApiResponse<T>> = flow {
        emit(ApiResponse.Loading)

        withTimeoutOrNull(20000L){
            val response = call()

            try{
                if(response.isSuccessful){
                    response.body()?.let {
                            data-> emit(ApiResponse.Success(data))
                    }
                }else{
                    response.errorBody()?.let { error ->
                        error.close()
                        val parsedError: Error =
                            Gson().fromJson(error.charStream(), Error::class.java)
                        emit(ApiResponse.Failure(parsedError.code ,parsedError.message))
                    }
                }
            }catch (e:Exception){
                emit(ApiResponse.Failure(400, e.message ?: e.toString()))
            }
        } ?:  emit(ApiResponse.Failure(408,"Время вышло! Пожалуйста повторите попытку снова."))
    }.flowOn(Dispatchers.IO)
