package KlepetChat.WebApi.Interceptor

import KlepetChat.DataSore.Context.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject  constructor(
    private val dataStoreManager: DataStoreManager
):Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val userData = runBlocking {
            dataStoreManager.userDataFlow.first()
        }
        val request = chain.request().newBuilder()
        request.addHeader("Authorization", "Bearer ${userData.accessToken}")
        return chain.proceed(request.build())
    }
}