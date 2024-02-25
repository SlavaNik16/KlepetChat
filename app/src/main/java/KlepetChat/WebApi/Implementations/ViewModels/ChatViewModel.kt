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
): BaseViewModel() {
    private val chatsResponse = MutableLiveData<ApiResponse<MutableList<Chat>>>()
    private val chatExists = MutableLiveData<ApiResponse<Boolean>>()
    val chats = chatsResponse
    val exists = chatExists

    fun getChats( coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        chats,
        coroutineErrorHandler
    ){
        chatRepository.getChats()
    }
    fun postFavorites(userId: UUID, coroutineErrorHandler: ICoroutinesErrorHandler) = BaseRequest(
        chatExists,
        coroutineErrorHandler
    ){
        chatRepository.postFavorites(userId)
    }
}
