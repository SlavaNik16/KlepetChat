package KlepetChat.WebApi.Implementations

import KlepetChat.WebApi.Models.Exceptions.Error
import KlepetChat.WebApi.Models.Response.Token
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

    /**
     * Функция выполняет вызовы API,
     * в потоке ввода/ввывода,
     * @param flow - создание асинхронного потока в функции flow
     * @param withTimeoutOrNull - Тайм-аут(20 сек) остановки
     * @param emit - имитируется получение объектов из бд
     */
    fun<T> ApiRequestFlowResponse(call: suspend () -> Response<T>): Flow<ApiResponse<T>> = flow {
        emit(ApiResponse.Loading)

        withTimeoutOrNull(20000L){
            val response = call()

            try{
                Log.d("POST","ResponseTry: ${response.isSuccessful}")
                if(response.isSuccessful){
                    Log.d("POST","Response: ${response.body()}")
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


//    /**
//     * Функция выполняет вызовы API,
//     * в потоке ввода/ввывода,
//     * @param flow - создание асинхронного потока в функции flow
//     * @param withTimeoutOrNull - Тайм-аут(20 сек) остановки
//     * @param emit - имитируется получение объектов из бд
//     */
//    fun<T> ApiRequestFlowCall(call: suspend () -> Call<T>): Flow<ApiResponse<T>> = flow {
//        emit(ApiResponse.Loading)
//
//        withTimeoutOrNull(20000L){
//            try{
//                call.enqueue(object : Callback<T>{
//                    override fun onResponse(call: Call<T>, response: Response<T>) {
//                        response.body()?.let {
//                            data -> ApiResponse.Success(data)
//                        }
//                    }
//
//                    override fun onFailure(call: Call<T>, t: Throwable) {
//                        val parsedError: Error =
//                            Gson().fromJson(t.localizedMessage, Error::class.java)
//                        ApiResponse.Failure(parsedError.code ,parsedError.message)
//                    }
//                })
//            }catch (e:Exception){
//                emit(ApiResponse.Failure(400, e.message ?: e.toString()))
//            }
//        } ?:  emit(ApiResponse.Failure(408,"Время вышло! Пожалуйста повторите попытку снова."))
//    }.flowOn(Dispatchers.IO)