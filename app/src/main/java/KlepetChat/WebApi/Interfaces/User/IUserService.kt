package KlepetChat.WebApi.Interfaces.User

import KlepetChat.WebApi.Models.Response.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IUserService {
    @GET("user/phone")
    suspend fun getByPhone(@Query("phone") phone:String): Response<User>
}