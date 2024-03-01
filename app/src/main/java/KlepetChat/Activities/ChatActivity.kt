package KlepetChat.Activities

import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.ChatAdapter
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.MessageViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Message
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.databinding.ActivityChatBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID


@AndroidEntryPoint
class ChatActivity : ComponentActivity() {
    private lateinit var binding: ActivityChatBinding

    private val chatViewModel: ChatViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()

    private var isPrev: Boolean = false;
    private lateinit var chatId:UUID
    private lateinit var phone:String

    private lateinit var messages: MutableList<Message>
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        init()
        messageViewModel.message.observe(this){
            when (it) {
                is ApiResponse.Success -> {
                    EventUpdateMessages(it.data)
                }

                is ApiResponse.Failure -> {
                    Toast.makeText(
                        this@ChatActivity, "Ошибка! ${it.message}", Toast.LENGTH_SHORT
                    ).show()
                }

                is ApiResponse.Loading -> {
                    return@observe
                }
            }
        }
        messageViewModel.messages.observe(this){
            when (it) {
                is ApiResponse.Success -> {
                    messages = it.data
                    EventUpdateMessages()
                }

                is ApiResponse.Failure -> {
                    Toast.makeText(
                        this@ChatActivity, "Ошибка! ${it.message}", Toast.LENGTH_SHORT
                    ).show()
                }

                is ApiResponse.Loading -> {
                    return@observe
                }
            }
        }
        chatViewModel.chat.observe(this) {
            when (it) {
                is ApiResponse.Success -> {
                    chatId = it.data.id
                    sendMessage(it.data.id)
                }

                is ApiResponse.Failure -> {
                    Toast.makeText(
                        this@ChatActivity, "Ошибка! ${it.message}", Toast.LENGTH_SHORT
                    ).show()
                }

                is ApiResponse.Loading -> {
                    return@observe
                }
            }
        }

    }

    private fun init() {
        messages = mutableListOf()
        var argument = intent.extras

        phone = argument?.getString(Constants.KEY_USER_PHONE).toString()
        chatAdapter = ChatAdapter(this, messages, phone)
        binding.recyclerChat.adapter = chatAdapter
        EventUpdateMessages()
        var txtName = argument?.getString(Constants.KEY_CHAT_NAME)
        isPrev = argument?.getBoolean(Constants.KEY_IS_PREV) == true
        if(!isPrev){
            var chatIdStr = argument?.getString(Constants.KEY_CHAT_ID).toString()
            chatId = UUID.fromString(chatIdStr)
            getMessages(chatId)
        }
        binding.txtName.text = txtName

    }

    private fun setListeners(){
        binding.back.setOnClickListener { onBackPress() }
        binding.sendMessage.setOnClickListener { onSendMessage() }
    }
    private fun onBackPress() {
        var intent = Intent(this@ChatActivity, MainActivity::class.java)
        startActivity(intent)
    }
    private fun onSendMessage(){
        if (binding.inputMessage.text.isBlank()) {
            return
        }
        if (!isPrev) {
            sendMessage(chatId)
            return
        }
        isPrev = false
        initChat()
    }

    private fun initChat(){
        var phone = intent.extras?.getString(Constants.KEY_USER_PHONE)
        chatViewModel.postContact(phone ?: "",
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ChatActivity, "Ошибка! ${message}", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    private fun sendMessage(chatId:UUID){
        messageViewModel.createMessage(chatId,
            binding.inputMessage.text.toString(),
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ChatActivity, "Ошибка! $message", Toast.LENGTH_SHORT
                    ).show()
                }
            })
        binding.inputMessage.text.clear()
    }
    private fun getMessages(chatId:UUID){
        messageViewModel.getMessagesWithChatId(chatId,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ChatActivity, "Ошибка! $message", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun EventUpdateMessages(message: Message? = null){
        if(message != null) {
            messages.add(message)
        }
        messages.sortBy { it.createdAt }
        if (messages.size != 0) {
            chatAdapter = ChatAdapter(this, messages, phone)
            binding.recyclerChat.adapter = chatAdapter
            chatAdapter.notifyDataSetChanged()
        }
        binding.progressBar.visibility = View.GONE
    }
}
