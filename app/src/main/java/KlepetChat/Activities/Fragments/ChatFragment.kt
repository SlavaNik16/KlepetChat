import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.ChatAdapter
import KlepetChat.DataSore.Models.UserData
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.MessageViewModel
import KlepetChat.WebApi.Implementations.ViewModels.SignalR.SignalRViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import KlepetChat.WebApi.Models.Response.Message
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.klepetchat.databinding.FragmentChatBinding
import java.util.UUID


class ChatFragment : Fragment() {

    var binding: FragmentChatBinding? = null
    private val messageViewModel: MessageViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    val signalRViewModel: SignalRViewModel by activityViewModels()

    var chatId: UUID = Constants.GUID_NULL

    private lateinit var phone: String
    private var chatType: ChatTypes = ChatTypes.Favorites
    private lateinit var messages: MutableList<Message>
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var initChat: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(inflater)
        setListeners()
        setObserve()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onHandlers()
        init()
    }

    private fun init() {
        chatId = UUID.fromString(arguments?.getString(Constants.KEY_CHAT_ID))
        messages = mutableListOf()
        validateChatId()

    }

    private fun onHandlers() {
        signalRViewModel.getConnection().on(
            "ReceiveMessage",
            { onHandlerReceiveMessage(it) }, Message::class.java
        )
    }

    private fun onHandlerReceiveMessage(it: Message) {
        requireActivity()
            .runOnUiThread(Runnable {
                EventUpdateMessages(it)
            })
    }

    private fun validateChatId() {
        if (chatId == Constants.GUID_NULL) {
            binding?.buttonInitChat?.visibility = View.VISIBLE
            return
        }
        joinGroup()
    }

    fun joinGroup() {
        if (chatType != ChatTypes.Favorites) {
            signalRViewModel.start(chatId.toString())
        }
        binding?.buttonInitChat?.visibility = View.GONE
        getMessages(chatId)
    }

    private fun setListeners() {
        binding?.sendMessage?.setOnClickListener { onSendMessage() }
        binding?.buttonInitChat?.setOnClickListener { initChat() }
        binding?.sendEmoticon?.setOnClickListener { }
    }


    private fun setObserve() {
        messageViewModel.message.observe(requireActivity()) { getMessage(it) }
        messageViewModel.messages.observe(requireActivity()) { getMessages(it) }
        userDataViewModel.userData.observe(requireActivity()) { getUser(it) }
    }

    private fun getUser(userData: UserData?) {
        if (userData != null) {
            phone = userData.phone
        }

    }

    private fun getMessages(chatId: UUID) {
        messageViewModel.getMessagesWithChatId(chatId,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        requireContext(), "Ошибка! $message", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun getMessages(api: ApiResponse<MutableList<Message>>) {
        when (api) {
            is ApiResponse.Success -> {
                messages = api.data
                EventUpdateMessages()
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    requireContext(), "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }


    private fun removeListeners() {
        binding?.sendMessage?.setOnClickListener(null)
        binding?.buttonInitChat?.setOnClickListener(null)
        binding?.sendEmoticon?.setOnClickListener(null)
        messages.clear()
        binding?.recyclerChat?.adapter = null
        binding?.recyclerChat?.layoutManager = null
        binding?.recyclerChat?.recycledViewPool?.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        binding = null
    }


    private fun getMessage(api: ApiResponse<Message>) {
        when (api) {
            is ApiResponse.Success -> {
                EventUpdateMessages(api.data)
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    requireContext(), "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }


    private fun EventUpdateMessages(message: Message? = null) {
        if (message != null) {
            messages.add(message)
        }
        messages.sortBy { it.createdAt }
        if (messages.size != 0) {
            chatAdapter = ChatAdapter(messages, phone)
            binding?.recyclerChat?.adapter = chatAdapter
            chatAdapter.notifyDataSetChanged()
        }
    }

    private fun onSendMessage() {
        if (binding?.inputMessage?.text.isNullOrBlank()) {
            return
        }
        if (chatType == ChatTypes.Favorites) {
            sendMessage(chatId)
            return
        }
        sendMessageSignalR(chatId)
    }

    private fun sendMessage(chatId: UUID) {
        messageViewModel.createMessage(chatId,
            binding?.inputMessage?.text.toString(),
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        requireContext(), "Ошибка! $message", Toast.LENGTH_SHORT
                    ).show()
                }
            })
        binding?.inputMessage?.text?.clear()
    }

    private fun sendMessageSignalR(chatId: UUID) {
        signalRViewModel.sendMessage(chatId,
            binding?.inputMessage?.text.toString(),
            chatId.toString(),
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        requireContext(), "Ошибка! $message", Toast.LENGTH_SHORT
                    ).show()
                }
            })
        binding?.inputMessage?.text?.clear()
    }

    fun leaveGroup() {
        signalRViewModel.close(chatId.toString())
    }


    companion object {

        @JvmStatic
        fun newInstance(chatId: UUID, type: ChatTypes = ChatTypes.Favorites) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    chatType = type
                    putString(Constants.KEY_CHAT_ID, chatId.toString())
                }
            }

        @JvmStatic
        fun newInstanceInit(onInitChat: () -> Unit) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    initChat = onInitChat
                    putString(Constants.KEY_CHAT_ID, Constants.GUID_NULL.toString())
                }
            }

    }
}