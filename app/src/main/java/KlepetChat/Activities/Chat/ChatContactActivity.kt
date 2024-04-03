package KlepetChat.Activities.Chat

import ChatFragment
import KlepetChat.Activities.Data.Constants
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.MessageViewModel
import KlepetChat.WebApi.Implementations.ViewModels.SignalR.SignalRViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import KlepetChat.WebApi.Models.Response.User
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
import okhttp3.ResponseBody
import java.util.Timer
import java.util.TimerTask
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


    fun signalNotification(signalRViewModel: SignalRViewModel, message: String, isSend: Boolean) {
        if (!isSend) {
            return
        }
        signalRViewModel.sendNotificationGroupContact(phoneOther!!, chatId!!, message)
        signalRViewModel.updateChat(phoneOther!!)
    }

    private fun setObserve() {
        chatViewModel.chat.observe(this) { getChat(it) }
        chatViewModel.deleteChat.observe(this) { getDeletedChat(it) }
        messageViewModel.exist.observe(this) { getDeletedMessage(it) }
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
                .into(binding?.butMenu)
        }
        binding?.textDesc?.text = "Не в сети"

    }

    override fun onStart() {
        super.onStart()
        statusOnline()
        statusOffline()
        statusPrint()
    }

    private fun statusOnline() {
        fragment?.signalRViewModel?.getConnection()?.on("StatusUsersOnline", { user ->
            runOnUiThread(Runnable {
                binding?.textDesc?.text = "В сети"
            })
        }, User::class.java)
    }

    private fun statusOffline() {
        fragment?.signalRViewModel?.getConnection()?.on("StatusUsersOffline", { user ->
            runOnUiThread(Runnable {
                if(user.phone != phoneOther) {
                    binding?.textDesc?.text = "Не в сети"
                }
            })
        }, User::class.java)
    }

    private fun statusPrint() {
        fragment?.signalRViewModel?.getConnection()?.on("StatusPrint", { user, isStart ->
            runOnUiThread(Runnable {
                if (user.phone == phoneOther) {
                    statisPrint = isStart
                    animationUpload()
                }
            })
        }, User::class.java, Boolean::class.java)
    }

    private var statisPrint = false
    private fun animationUpload() {
        runOnUiThread {
            if (!statisPrint) {
                binding?.textDesc?.text = "В сети"
                return@runOnUiThread
            }
            var timer = Timer()
            var Delay: Long = 230
            var DelayThirst: Long = 700
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        binding?.textDesc?.text = "печатает."
                    }
                },
                Delay
            )
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        binding?.textDesc?.text = "печатает.."
                    }
                },
                Delay * 2
            )
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        binding?.textDesc?.text = "печатает..."
                    }
                },
                Delay * 3
            )

            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        if (!statisPrint) {
                            binding?.textDesc?.text = "В сети"
                            return
                        }
                        animationUpload()
                    }
                },
                DelayThirst
            )
        }
    }

    override fun onStop() {
        super.onStop()
        fragment?.signalRViewModel?.getConnection()?.remove("StatusUsersOnline")
        fragment?.signalRViewModel?.getConnection()?.remove("StatusUsersOffline")
        fragment?.signalRViewModel?.getConnection()?.remove("StatusPrint")
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
    }

    private fun deletedChat() {
        chatViewModel.deleteChat(chatId!!,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
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
                fragment?.joinGroup(ChatTypes.Contact)
                binding?.butMenu?.visibility = View.VISIBLE
                fragment?.signalRViewModel?.updateChat(phoneOther!!)
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

    private fun getDeletedMessage(api: ApiResponse<ResponseBody>) {
        when (api) {
            is ApiResponse.Success -> {
                fragment?.signalRViewModel?.updateMessage(phoneOther.toString())
                fragment?.signalRViewModel?.updateChat(phoneOther.toString())

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

    private fun getDeletedChat(api: ApiResponse<ResponseBody>) {
        when (api) {
            is ApiResponse.Success -> {
                fragment?.signalRViewModel?.updateChat(phoneOther.toString())
                onBackPress()
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
