package KlepetChat.WebApi.Interfaces

import KlepetChat.WebApi.Models.Response.Message
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.UUID

interface IMessageService {
    @POST("message")
    suspend fun createMessage(@Query("chatId") chatId: UUID, @Query("message") message:String): Response<Message>
}