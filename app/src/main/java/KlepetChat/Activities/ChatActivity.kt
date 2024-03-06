package KlepetChat.Activities

import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.ChatAdapter
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.MessageViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import KlepetChat.WebApi.Models.Response.Message
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityChatBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID


@AndroidEntryPoint
class ChatActivity : ComponentActivity() {
    private var binding: ActivityChatBinding? = null

    private val chatViewModel: ChatViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()

    private var isPrev: Boolean = false;
    private lateinit var chatId: UUID
    private lateinit var phone: String

    private lateinit var messages: MutableList<Message>
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setListeners()
        setObserve()
        init()

    }

    private fun getMessage(api: ApiResponse<Message>) {
        when (api) {
            is ApiResponse.Success -> {
                EventUpdateMessages(api.data)
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ChatActivity, "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getMessages(api: ApiResponse<MutableList<Message>>) {
        when (api) {
            is ApiResponse.Success -> {
                messages = api.data
                EventUpdateMessages()
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ChatActivity, "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getChat(api: ApiResponse<Chat>) {
        when (api) {
            is ApiResponse.Success -> {
                chatId = api.data.id
                sendMessage(chatId)
                EventUpdateMessages()
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ChatActivity, "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun setObserve() {
        messageViewModel.message.observe(this) { getMessage(it) }
        messageViewModel.messages.observe(this) { getMessages(it) }
        chatViewModel.chat.observe(this) { getChat(it) }
    }

    private fun init() {
        messages = mutableListOf()
        var argument = intent.extras
        phone = argument?.getString(Constants.KEY_USER_PHONE).toString()
        chatAdapter = ChatAdapter(this, messages, phone)
        binding?.recyclerChat?.adapter = chatAdapter
        EventUpdateMessages()
        var txtName = argument?.getString(Constants.KEY_CHAT_NAME)
        binding?.txtName?.text = txtName

        var imageChat = argument?.getString(Constants.KEY_IMAGE_URL)
        if (!imageChat.isNullOrBlank()) {
            Picasso.get()
                .load(imageChat)
                .placeholder(R.drawable.baseline_account_circle_24)
                .error(R.drawable.baseline_account_circle_24)
                .into(binding?.imageChat)
        }
        var chatType = argument?.getString(Constants.KEY_CHAT_TYPE).toString()
        when (chatType) {
            ChatTypes.Contact.name -> binding?.textDesc?.text = "В сети"
            ChatTypes.Group.name -> binding?.textDesc?.text = "20 подписчиков"
            ChatTypes.Favorites.name -> {
                binding?.textDesc?.visibility = View.GONE
                binding?.imageChat?.setImageResource(R.drawable.favorites_icon)
            }
        }
        isPrev = argument?.getBoolean(Constants.KEY_IS_PREV) == true
        if (!isPrev) {
            var chatIdStr = argument?.getString(Constants.KEY_CHAT_ID).toString()
            chatId = UUID.fromString(chatIdStr)
            getMessages(chatId)
        }

    }

    private fun setListeners() {
        binding?.back?.setOnClickListener { onBackPress() }
        binding?.sendMessage?.setOnClickListener { onSendMessage() }
    }

    private fun removeListeners() {
        binding?.back?.setOnClickListener(null)
        binding?.sendMessage?.setOnClickListener(null)
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

    private fun onBackPress() {
        var intent = Intent(this@ChatActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onSendMessage() {
        if (binding?.inputMessage?.text.isNullOrBlank()) {
            return
        }
        if (!isPrev) {
            sendMessage(chatId)
            return
        }
        isPrev = false
        initChat()
    }

    private fun initChat() {
        var phone = intent.extras?.getString(Constants.KEY_USER_PHONE)
        if (phone.isNullOrBlank()) {
            return
        }
        chatViewModel.postContact(phone,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ChatActivity, "Ошибка! ${message}", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun sendMessage(chatId: UUID) {
        messageViewModel.createMessage(chatId,
            binding?.inputMessage?.text.toString(),
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ChatActivity, "Ошибка! $message", Toast.LENGTH_SHORT
                    ).show()
                }
            })
        binding?.inputMessage?.text?.clear()
    }

    private fun getMessages(chatId: UUID) {
        messageViewModel.getMessagesWithChatId(chatId,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ChatActivity, "Ошибка! $message", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun EventUpdateMessages(message: Message? = null) {
        if (message != null) {
            messages.add(message)
        }
        messages.sortBy { it.createdAt }
        if (messages.size != 0) {
            chatAdapter = ChatAdapter(this, messages, phone)
            binding?.recyclerChat?.adapter = chatAdapter
            chatAdapter.notifyDataSetChanged()
        }
        binding?.progressBar?.visibility = View.GONE
    }
}
