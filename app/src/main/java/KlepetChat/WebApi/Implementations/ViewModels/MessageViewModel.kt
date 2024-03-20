package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.BaseViewModel
import KlepetChat.WebApi.Implementations.Repositories.MessageRepository
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Message
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
) : BaseViewModel() {
    private val messageResponse = MutableLiveData<ApiResponse<Message>>()
    private val messagesResponse = MutableLiveData<ApiResponse<MutableList<Message>>>()
    private val messagesResponseBody = MutableLiveData<ApiResponse<ResponseBody>>()
    val message = messageResponse
    val messages = messagesResponse
    var exist = messagesResponseBody


    fun getMessagesWithChatId(chatId: UUID, coroutineErrorHandler: ICoroutinesErrorHandler) =
        BaseRequest(
            messagesResponse,
            coroutineErrorHandler
        ) {
            messageRepository.getMessagesWithChatId(chatId)
        }

    fun createMessage(
        chatId: UUID,
        message: String,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        messageResponse,
        coroutineErrorHandler
    ) {
        messageRepository.createMessage(chatId, message)
    }

    fun deleteMessages(
        chatId: UUID,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        messagesResponseBody,
        coroutineErrorHandler
    ) {
        messageRepository.deleteMessages(chatId)
    }
}