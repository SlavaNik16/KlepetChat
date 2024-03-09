package KlepetChat.Activities.Chat

import ChatFragment
import KlepetChat.Activities.Data.Constants
import KlepetChat.Activities.MainActivity
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityChatGroupBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID


@AndroidEntryPoint
class ChatGroupActivity : AppCompatActivity() {
    private var binding: ActivityChatGroupBinding? = null

    private val chatViewModel: ChatViewModel by viewModels()

    private lateinit var chatId: UUID

    private lateinit var fragment: ChatFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatGroupBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setListeners()
        setObserve()
        init()
        Toast.makeText(this,"$chatId", Toast.LENGTH_SHORT).show()
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

        val persons = argument?.getStringArrayList(Constants.KEY_CHAT_PEOPLE)
        binding?.textDesc?.text = "${persons?.count()} подписчик(-a)"
        val phone = argument?.getString(Constants.KEY_USER_PHONE)
        val chatIdStr = argument?.getString(Constants.KEY_CHAT_ID)
        chatId = UUID.fromString(chatIdStr)
        if(persons!!.contains(phone)){
            fragment = ChatFragment.newInstance(chatId)
            fragmentInstance(fragment)
        }else{
            fragment = ChatFragment.newInstanceInit() { onInitChat() }
            fragmentInstance(fragment)
        }

        val txtName = argument.getString(Constants.KEY_CHAT_NAME)
        binding?.txtName?.text = txtName

        val imageChat = argument.getString(Constants.KEY_IMAGE_URL)
        if (!imageChat.isNullOrBlank()) {
            Picasso.get()
                .load(imageChat)
                .placeholder(R.drawable.baseline_account_circle_24)
                .error(R.drawable.baseline_account_circle_24)
                .into(binding?.imageChat)
        }

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
        var intent = Intent(this@ChatGroupActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onInitChat() {
        chatViewModel.postJoinGroup(chatId,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ChatGroupActivity, "Ошибка! $message", Toast.LENGTH_SHORT
                    ).show()
                }
            })
        Toast.makeText(this,"$chatId", Toast.LENGTH_SHORT).show()
    }

    private fun getChat(api: ApiResponse<Chat>) {
        when (api) {
            is ApiResponse.Success -> {
                chatId = api.data.id
                fragment.binding?.buttonInitChat?.visibility = View.GONE
                fragment.chatId = chatId
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ChatGroupActivity, "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }
}
