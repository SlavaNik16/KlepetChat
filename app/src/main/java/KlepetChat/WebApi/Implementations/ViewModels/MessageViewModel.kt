package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.BaseViewModel
import KlepetChat.WebApi.Implementations.Repositories.MessageRepository
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Message
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
): BaseViewModel() {
    private val messageResponse = MutableLiveData<ApiResponse<Message>>()
    val message = messageResponse


    fun createMessage(chatId: UUID, message: String, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        messageResponse,
        coroutineErrorHandler
    ) {
        messageRepository.createMessage(chatId, message)
    }
}