package KlepetChat.Activities

import KlepetChat.Activities.Data.Constants
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.MessageViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.databinding.ActivityChatBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID


@AndroidEntryPoint
class ChatActivity : ComponentActivity() {
    private lateinit var binding: ActivityChatBinding
    private var isPrev: Boolean = false;
    private val chatViewModel: ChatViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()
    private lateinit var chatId:UUID
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageViewModel.messages.observe(this){
            when (it) {
                is ApiResponse.Success -> {
                    Log.d("Message", "${it.data}")
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
        loadDetails()
        chatViewModel.chat.observe(this) {
            when (it) {
                is ApiResponse.Success -> {
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
        binding.sendMessage.setOnClickListener {
            if (binding.inputMessage.text.isBlank()) {
                return@setOnClickListener
            }
            if (!isPrev) {
                try {
                    sendMessage(chatId)
                }catch (ex:Exception){
                    Toast.makeText(
                        this@ChatActivity, "Непредвиденная ошибка!! ${ex}", Toast.LENGTH_SHORT
                    ).show()
                }
                return@setOnClickListener
            }
            isPrev = false
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
        binding.back.setOnClickListener {
            var intent = Intent(this@ChatActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadDetails() {
        var argument = intent.extras
        var txtName = argument?.getString(Constants.KEY_CHAT_NAME)
        isPrev = argument?.getBoolean(Constants.KEY_IS_PREV) == true
        if(!isPrev){
            var chatIdStr = argument?.getString(Constants.KEY_CHAT_ID).toString()
            chatId = UUID.fromString(chatIdStr)
            getMessages(chatId)
        }
        binding.txtName.text = txtName
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
}
