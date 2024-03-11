package KlepetChat.WebApi.Implementations.ViewModels

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.BaseViewModel
import KlepetChat.WebApi.Implementations.Repositories.ChatRepository
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : BaseViewModel() {
    private val chatsResponse = MutableLiveData<ApiResponse<MutableList<Chat>>>()
    private val chatExists = MutableLiveData<ApiResponse<Boolean>>()
    private val chatResponse = MutableLiveData<ApiResponse<Chat>>()
    val chats = chatsResponse
    val exists = chatExists
    val chat = chatResponse

    fun getChats(coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        chatsResponse,
        coroutineErrorHandler
    ) {
        chatRepository.getChats()
    }

    fun getChatsByName(name: String, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        chatsResponse,
        coroutineErrorHandler
    ) {
        chatRepository.getChatsByName(name)
    }

    fun postFavorites(userId: UUID, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        chatExists,
        coroutineErrorHandler
    ) {
        chatRepository.postFavorites(userId)
    }

    fun postContact(phoneOther: String, coroutineErrorHandler: ICoroutinesErrorHandler) =
        BaseRequest(
            chatResponse,
            coroutineErrorHandler
        ) {
            chatRepository.postContact(phoneOther)
        }

    fun postGroup(
        name: String,
        photo: String? = null,
        coroutineErrorHandler: ICoroutinesErrorHandler,
    ) = BaseRequest(
        chatResponse,
        coroutineErrorHandler
    ) {
        chatRepository.postGroup(name, photo)
    }

    fun postJoinGroup(id: UUID, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        chatResponse,
        coroutineErrorHandler
    ) {
        chatRepository.postJoinGroup(id)
    }
}
