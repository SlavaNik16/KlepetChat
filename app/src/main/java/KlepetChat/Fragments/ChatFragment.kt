
import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.ChatAdapter
import KlepetChat.DataSore.Models.UserData
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.MessageViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
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

    private var chatId: UUID = Constants.GUID_NULL

    private lateinit var phone: String
    private lateinit var messages: MutableList<Message>
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var initChat: () -> Unit
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(inflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObserve()
        init()
    }

    private fun init() {
        chatId = UUID.fromString(arguments?.getString(Constants.KEY_CHAT_ID))
        messages = mutableListOf()
        validateChatId()
    }

    private fun validateChatId() {
        if (chatId == Constants.GUID_NULL) {
            binding?.buttonInitChat?.visibility = View.VISIBLE
            return
        }
        binding?.buttonInitChat?.visibility = View.GONE
        getMessages(chatId)
    }

    private fun setListeners() {
        binding?.sendMessage?.setOnClickListener { onSendMessage() }
        binding?.buttonInitChat?.setOnClickListener { initChat() }
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
            chatAdapter = ChatAdapter(requireContext(), messages, phone)
            binding?.recyclerChat?.adapter = chatAdapter
            chatAdapter.notifyDataSetChanged()
        }
    }

    private fun onSendMessage() {
        if (binding?.inputMessage?.text.isNullOrBlank()) {
            return
        }
        sendMessage(chatId)
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


    companion object {

        @JvmStatic
        fun newInstance(chatId: UUID) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.KEY_CHAT_ID, chatId.toString())
                }
            }

        @JvmStatic
        fun newInstanceContactInit(onInitChat: () -> Unit) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    initChat = onInitChat
                    putString(Constants.KEY_CHAT_ID, Constants.GUID_NULL.toString())
                }
            }

    }
}