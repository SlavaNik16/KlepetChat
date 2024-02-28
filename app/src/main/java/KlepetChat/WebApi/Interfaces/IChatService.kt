package KlepetChat.WebApi.Interfaces

import KlepetChat.WebApi.Models.Response.Chat
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.UUID

interface IChatService {

    @GET("chat")
    suspend fun getChats():Response<MutableList<Chat>>

    @POST("chat/favorites")
    suspend fun postFavorites(@Query("userId") userId: UUID):Response<Boolean>

    @POST("chat/contact")
    suspend fun postContact(@Query("phoneOther") phoneOther: String):Response<Chat>
}