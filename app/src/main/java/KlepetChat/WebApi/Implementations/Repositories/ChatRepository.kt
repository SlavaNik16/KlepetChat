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

    fun postContact(phoneOther:String) = ApiRequestFlowResponse {
        chatService.postContact(phoneOther)
    }

    fun postGroup(name:String) = ApiRequestFlowResponse {
        chatService.postGroup(name)
    }

    fun getChats() = ApiRequestFlowResponse {
        chatService.getChats()
    }
}