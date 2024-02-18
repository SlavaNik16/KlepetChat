package KlepetChat.WebApi.Implementations.Authentificator

import KlepetChat.DataSore.Context.DataStoreManager
import KlepetChat.WebApi.Interfaces.ITokenService
import KlepetChat.WebApi.Models.Response.Token
import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val userData = runBlocking {
            dataStoreManager.userDataFlow.first()
        }
        Log.d("Debug", "tokeRefresh")
        return runBlocking {

            Log.d("Auth", "tokeRefresh")
            val token = Token(userData.accessToken, userData.refreshToken)
            val newToken = PostRefreshToken(token)

            if (!newToken.isSuccessful || newToken.body() == null) {
                dataStoreManager.UpdateTokens("", "")
            }
            newToken.body()?.let {
                dataStoreManager.UpdateTokens(it.accessToken, it.refreshToken)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${it.accessToken}")
                    .build()
            }
        }
    }
    private suspend fun PostRefreshToken(token: Token): retrofit2.Response<Token> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://klepetapi.somee.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service = retrofit.create(ITokenService::class.java)
        return service.postRefresh(token)
    }
}