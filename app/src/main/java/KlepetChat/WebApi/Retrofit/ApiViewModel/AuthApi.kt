package KlepetChat.WebApi.Retrofit.ApiViewModel

import KlepetChat.WebApi.Interfaces.IAuthService
import KlepetChat.WebApi.Retrofit.ApiRetrofit
import javax.inject.Inject

class AuthApi {
    private var retrofit = ApiRetrofit().GetService()

    fun GetAuth(): IAuthService {
        return retrofit.create(IAuthService::class.java)
    }
}