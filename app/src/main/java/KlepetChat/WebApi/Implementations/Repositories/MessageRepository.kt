package KlepetChat.WebApi.Implementations.Repositories

import KlepetChat.WebApi.Implementations.ApiRequestFlowResponse
import KlepetChat.WebApi.Interfaces.IMessageService
import java.util.UUID
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val messageService: IMessageService,
) {

    fun getMessagesWithChatId(chatId: UUID) = ApiRequestFlowResponse {
        messageService.getMessagesWithChatId(chatId)
    }

    fun createMessage(chatId: UUID, message: String) = ApiRequestFlowResponse {
        messageService.createMessage(chatId, message)
    }
}