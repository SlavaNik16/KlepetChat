package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlowResponse
import KlepetChat.WebApi.Interfaces.IChatService
import java.util.UUID
import javax.inject.Inject


class ChatRepository @Inject constructor(
    private val chatService: IChatService,
) {
    fun postFavorites(userId:UUID) = ApiRequestFlowResponse {
        chatService.postFavorites(userId)
    }

    fun getChats() = ApiRequestFlowResponse {
        chatService.getChats()
    }

}