package KlepetChat.WebApi.Interfaces

import KlepetChat.WebApi.Models.Request.UserRegister
import KlepetChat.WebApi.Models.Response.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IUserService {
    @GET("user/phone")
    suspend fun getByPhone(@Query("phone") phone:String): Response<User>

    @POST("user")
    suspend fun postCreate(@Body uerRegister:UserRegister): Response<User>
}