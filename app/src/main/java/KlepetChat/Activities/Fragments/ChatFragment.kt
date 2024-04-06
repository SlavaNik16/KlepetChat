
import KlepetChat.Activities.Chat.ChatContactActivity
import KlepetChat.Activities.Chat.ChatGroupActivity
import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.ChatAdapter
import KlepetChat.DataSore.Models.UserData
import KlepetChat.Utils.TextChangedListener
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.DataStore.UserDataViewModel
import KlepetChat.WebApi.Implementations.ViewModels.MessageViewModel
import KlepetChat.WebApi.Implementations.ViewModels.SignalR.SignalRViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import KlepetChat.WebApi.Models.Response.Message
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.klepetchat.databinding.FragmentChatBinding
import com.vanniktech.emoji.EmojiPopup
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

class ChatFragment : Fragment() {

    var binding: FragmentChatBinding? = null
    private val messageViewModel: MessageViewModel by activityViewModels()
    private val userDataViewModel: UserDataViewModel by activityViewModels()
    val signalRViewModel: SignalRViewModel by activityViewModels()

    var chatId: UUID = Constants.GUID_NULL
    private var chatType: ChatTypes = ChatTypes.Favorites

    private var phone: String? = null
    private var messages: MutableList<Message>? = null
    private var chatAdapter: ChatAdapter? = null
    private var emojiPopup: EmojiPopup? = null

    private lateinit var initChat: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(inflater)
        emojiPopup = EmojiPopup.Builder.fromRootView(binding?.root).build(binding!!.inputMessage);
        setListeners()
        setObserve()
        return binding!!.root
    }

    private fun onHandlerUpdateChat() {
        updateMessageHandler()
        deleteChatHandler()
    }
    private fun updateMessageHandler(){
        signalRViewModel.getConnection().on("UpdateMessage") {
            if(chatId == Constants.GUID_NULL){
                return@on
            }
            getMessages(chatId)
        }
    }
    private fun deleteChatHandler(){
        signalRViewModel.getConnection().on("ExitChat") {
            requireActivity().finish()
        }
    }
    override fun onStart() {
        super.onStart()
        onHandlerUpdateChat()
    }

    override fun onStop() {
        super.onStop()
        removeHandlerUpdateChat()
    }
    private fun removeHandlerUpdateChat() {
        signalRViewModel.getConnection().remove("UpdateMessage")
        signalRViewModel.getConnection().remove("ExitChat")
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
                if (chatType == ChatTypes.Contact) {
                    if (requireActivity() is ChatContactActivity) {
                        var chatContact = requireActivity() as ChatContactActivity
                        chatContact.signalNotification(signalRViewModel, it.text, it.phone == phone)
                    }
                }else if(chatType == ChatTypes.Group){
                    if (requireActivity() is ChatGroupActivity) {
                        var chatContact = requireActivity() as ChatGroupActivity
                        chatContact.signalNotification(signalRViewModel, it)
                    }
                }
            })
    }

    private fun validateChatId() {
        if (chatId == Constants.GUID_NULL) {
            binding?.buttonInitChat?.visibility = View.VISIBLE
            return
        }
        joinGroup(chatType)
    }

    fun joinGroup(chatTypes: ChatTypes) {
        chatType = chatTypes
        if (chatType != ChatTypes.Favorites) {
            signalRViewModel.joinGroup(chatId.toString())
        }
        binding?.buttonInitChat?.visibility = View.GONE
        getMessages(chatId)
    }

    private fun setListeners() {
        binding?.sendMessage?.setOnClickListener { onSendMessage() }
        binding?.buttonInitChat?.setOnClickListener { initChat() }
        binding?.sendEmoticon?.setOnClickListener { sendEmotionAction() }
        binding?.inputMessage?.addTextChangedListener(addTextMessageChange())
    }

    private fun addTextMessageChange(): TextWatcher {
        return object : TextChangedListener<EditText>(binding?.inputMessage!!) {
            private var timer = Timer()
            private val Delay: Long = 75
            private val DelayThink: Long = 1400
            private var isStart = false
            override fun onTextChanged(target: EditText, s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            if (!isStart) {
                                printStatusTrue()
                                isStart = true
                            }
                        }
                    },
                    Delay
                )
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            printStatusFalse()
                            isStart = false
                        }
                    },
                    DelayThink
                )
            }
        }
    }

    private fun printStatusTrue() {
        signalRViewModel.printGroup(chatId.toString(), true)
    }

    private fun printStatusFalse() {
        signalRViewModel.printGroup(chatId.toString(), false)
    }

    private fun sendEmotionAction() {
        if (emojiPopup?.isShowing == true) {
            emojiPopup?.dismiss()
            return
        }
        emojiPopup?.toggle()

    }


    private fun setObserve() {
        messageViewModel.message.observe(requireActivity()) { getMessage(it) }
        messageViewModel.messages.observe(requireActivity()) { getMessagesApi(it) }
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

    private fun getMessagesApi(api: ApiResponse<MutableList<Message>>) {
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
        binding?.recyclerChat?.adapter = null
        binding?.recyclerChat?.layoutManager = null
        binding?.recyclerChat?.recycledViewPool?.clear()
    }

    private fun removeComponent() {
        messages = null
        chatAdapter = null
        phone = null
        emojiPopup = null
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        removeComponent()
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
            messages?.add(message)
        }
        if (messages != null) {
            if(messages?.size != 0) {
                messages?.sortBy { it.createdAt }
            }
            chatAdapter = ChatAdapter(messages!!, phone!!)
            binding?.recyclerChat?.adapter = chatAdapter
            chatAdapter?.notifyDataSetChanged()
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
        signalRViewModel.sendMessage(
            chatId,
            binding?.inputMessage?.text.toString(),
            chatId.toString()
        )
        binding?.inputMessage?.text?.clear()
    }

    fun leaveGroup() {
        signalRViewModel.leaveGroup(chatId.toString())
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