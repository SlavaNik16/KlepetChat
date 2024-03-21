package KlepetChat.Activities.Chat

import ChatFragment
import KlepetChat.Activities.Data.Constants
import KlepetChat.Activities.MainActivity
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.MessageViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
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
    private val messageViewModel: MessageViewModel by viewModels()

    private var chatId: UUID? = null
    private var phoneOther: String? = null

    private var fragment: ChatFragment? = null
    private var popupMenu: PopupMenu? = null
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
        if (!chatIdStr.isNullOrBlank()) {
            chatId = UUID.fromString(chatIdStr)
            fragment = ChatFragment.newInstance(chatId!!, ChatTypes.Contact)
            fragmentInstance(fragment!!)
            binding?.butMenu?.visibility = View.VISIBLE
        } else {
            fragment = ChatFragment.newInstanceInit() { onInitChat() }
            fragmentInstance(fragment!!)
            binding?.butMenu?.visibility = View.INVISIBLE
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
        binding?.textDesc?.text = "Не в сети"

    }

    override fun onStart() {
        super.onStart()
        fragment?.signalRViewModel?.getConnection()?.on("Status", { connectionId, isStatus ->
            runOnUiThread(Runnable {
                if (connectionId != fragment?.signalRViewModel?.getConnection()?.connectionId) {
                    binding?.textDesc?.text = if (isStatus) "В сети" else "Не в сети"
                }
            })

        }, String::class.java, Boolean::class.java)
    }

    private fun setListeners() {
        binding?.back?.setOnClickListener { onBackPress() }
        binding?.butPhone?.setOnClickListener { onPhonePress() }
        binding?.butMenu?.setOnClickListener { onMenuPress() }
    }

    private fun onMenuPress() {
        popupMenu = PopupMenu(this@ChatContactActivity, binding!!.butMenu)
        popupMenu?.menuInflater?.inflate(R.menu.contracts_menu, popupMenu?.menu)

        popupMenu?.setOnMenuItemClickListener { onMenuItemClick(it) }
        popupMenu?.show()
    }

    private fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_clear -> {
                deletedMessages()
            }

            R.id.nav_delete -> {
                deletedChat()
            }
        }
        return true
    }

    private fun deletedMessages() {
        messageViewModel.deleteMessages(chatId!!,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
        finish()
        startActivity(intent)
    }

    private fun deletedChat() {
        chatViewModel.deleteChat(chatId!!,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
        onBackPress()
    }

    private fun onPhonePress() {
        val dial = "tel: $phoneOther"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
    }

    private fun removeListeners() {
        binding?.back?.setOnClickListener(null)
        binding?.butPhone?.setOnClickListener(null)
        binding?.butMenu?.setOnClickListener(null)
        popupMenu?.setOnMenuItemClickListener(null)
        popupMenu = null
    }

    private fun removeComponent() {
        popupMenu = null
        chatId = null
        phoneOther = null
        fragment?.onDestroy()
    }


    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        removeComponent()
        this.viewModelStore.clear()
        binding = null
    }

    override fun onPause() {
        super.onPause()
        fragment?.leaveGroup()
    }

    private fun onBackPress() {
        var intent = Intent(this@ChatContactActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onInitChat() {
        chatViewModel.postContact(phoneOther!!,
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
                fragment?.chatId = chatId!!
                fragment?.joinGroup()
                binding?.butMenu?.visibility = View.VISIBLE
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ChatContactActivity, "Ошибка! ${api.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }
}
