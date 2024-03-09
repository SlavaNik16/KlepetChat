package KlepetChat.Activities

import ChatFragment
import KlepetChat.Activities.Data.Constants
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityChatContactBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID


@AndroidEntryPoint
class ChatContactActivity : AppCompatActivity() {
    private var binding: ActivityChatContactBinding? = null

    private val chatViewModel: ChatViewModel by viewModels()

    private lateinit var chatId: UUID
    private lateinit var phoneOther: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatContactBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setListeners()
        setObserve()
        init()

    }

    private fun setObserve() {
        chatViewModel.chat.observe(this) { getChat(it) }
    }

    private fun fragmentInstance(f: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chatFragment, f)
            .commit()
    }


    private fun init() {
        val argument = intent.extras
        phoneOther = argument?.getString(Constants.KEY_USER_PHONE_OTHER).toString()

        val chatIdStr = argument?.getString(Constants.KEY_CHAT_ID)
        if(!chatIdStr.isNullOrBlank()){
            chatId = UUID.fromString(chatIdStr)
            val fragment = ChatFragment.newInstance(chatId)
            fragmentInstance(fragment)
        }else{
            val fragment = ChatFragment.newInstanceContactInit() { onInitChat() }
            fragmentInstance(fragment)
        }

        val txtName = argument?.getString(Constants.KEY_CHAT_NAME)
        binding?.txtName?.text = txtName

        val imageChat = argument?.getString(Constants.KEY_IMAGE_URL)
        if (!imageChat.isNullOrBlank()) {
            Picasso.get()
                .load(imageChat)
                .placeholder(R.drawable.baseline_account_circle_24)
                .error(R.drawable.baseline_account_circle_24)
                .into(binding?.imageChat)
        }
        binding?.textDesc?.text = "В сети"
    }

    private fun setListeners() {
        binding?.back?.setOnClickListener { onBackPress() }
    }

    private fun removeListeners() {
        binding?.back?.setOnClickListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        binding = null
    }

    private fun onBackPress() {
        var intent = Intent(this@ChatContactActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onInitChat() {
        chatViewModel.postContact(phoneOther,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                       this@ChatContactActivity, "Ошибка! $message", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun getChat(api: ApiResponse<Chat>) {
        when (api) {
            is ApiResponse.Success -> {
                chatId = api.data.id
                fragmentInstance(ChatFragment.newInstance(chatId))
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ChatContactActivity, "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }
}
