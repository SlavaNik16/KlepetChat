package KlepetChat.Activities.Chat

import ChatFragment
import KlepetChat.Activities.Data.Constants
import KlepetChat.Activities.DialogFragment.AlertDialogGroupChatProfile
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.MessageViewModel
import KlepetChat.WebApi.Implementations.ViewModels.SignalR.SignalRViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import KlepetChat.WebApi.Models.Response.Enums.RoleTypes
import KlepetChat.WebApi.Models.Response.Message
import KlepetChat.WebApi.Models.Response.User
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityChatGroupBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

@AndroidEntryPoint
class ChatGroupActivity : AppCompatActivity() {
    var binding: ActivityChatGroupBinding? = null

    private val chatViewModel: ChatViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()

    private var chatId: UUID? = null
    private var phone: String? = null
    private var image: String? = null
    private var roleType: RoleTypes = RoleTypes.User

    private var fragment: ChatFragment? = null
    private var popupMenu: PopupMenu? = null

    private var persons:MutableList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatGroupBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setListeners()
        setObserve()
        init()
    }
    fun signalNotification(signalRViewModel: SignalRViewModel, message: Message) {
        if (message.phone != phone) {
            return
        }
        for(person in persons!!){
            if(person == message.phone){
                continue
            }
            signalRViewModel.sendNotificationGroup(person, chatId!!, message.id)
            signalRViewModel.updateChat(person)
        }
    }


    private fun statusOnline() {
        fragment?.signalRViewModel?.getConnection()?.on("StatusUsersOnline", { user ->
            runOnUiThread(Runnable {

            })
        }, User::class.java)
    }

    private fun statusOffline() {
        fragment?.signalRViewModel?.getConnection()?.on("StatusUsersOffline", { user ->
            runOnUiThread(Runnable {

            })
        }, User::class.java)
    }

    private fun statusPrint() {
        fragment?.signalRViewModel?.getConnection()?.on("StatusPrint", { user, isStart ->
            runOnUiThread(Runnable {
                if (user.phone != phone) {
                    statusPrint = isStart
                    animationUpload(user)
                }
            })
        }, User::class.java, Boolean::class.java)
    }

    private var statusPrint = false
    private fun animationUpload(user: User) {
        runOnUiThread {
            if (!statusPrint) {
                binding?.textDesc?.text = "${persons!!.count()} подписчиков"
                return@runOnUiThread
            }
            var timer = Timer()
            var Delay: Long = 230
            var DelayThirst: Long = 700
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        binding?.textDesc?.text = "${user.surname} печатает."
                    }
                },
                Delay
            )
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        binding?.textDesc?.text = "${user.surname} печатает.."
                    }
                },
                Delay * 2
            )
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        binding?.textDesc?.text = "${user.surname} печатает..."
                    }
                },
                Delay * 3
            )

            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        if (!statusPrint) {
                            binding?.textDesc?.text ="${persons!!.count()} подписчиков"
                            return
                        }
                        animationUpload(user)
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

    private fun setObserve() {
        chatViewModel.chatJoin.observe(this) { getChatJoin(it) }
        chatViewModel.chat.observe(this) { getChatApi(it) }
        messageViewModel.exist.observe(this) { getDeletedMessage(it) }
        chatViewModel.exists.observe(this) { getLeaveChat(it) }
        chatViewModel.deleteChat.observe(this) { getDeletedChat(it) }
    }

    private fun fragmentInstance(f: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chatFragment, f)
            .commit()
    }

    private fun init() {
        val argument = intent.extras
        phone = argument?.getString(Constants.KEY_USER_PHONE)
        val chatIdStr = argument?.getString(Constants.KEY_CHAT_ID)
        chatId = UUID.fromString(chatIdStr)
        getChat(chatId!!)
    }
    private fun getChat(chatId: UUID){
       chatViewModel.getChatById(chatId,
           object : ICoroutinesErrorHandler{
               override fun onError(message: String) {

               }
           })
    }
    private fun initChat(chat: Chat){
        getChatUpdate(chat)
        if (persons!!.contains(phone)) {
            fragment = ChatFragment.newInstance(chatId!!, ChatTypes.Group)
            fragmentInstance(fragment!!)
            binding?.imageChat?.visibility = View.VISIBLE
        } else {
            fragment = ChatFragment.newInstanceInit() { onInitChat() }
            fragmentInstance(fragment!!)
            binding?.imageChat?.visibility = View.INVISIBLE
        }
        statusOnline()
        statusOffline()
        statusPrint()
    }

    fun getChatUpdate(chat: Chat){
        chatId = chat.id
        binding?.txtName?.text = chat.name
        image = chat.photo ?: "empty"
        if(!chat.photo.isNullOrBlank()){
            Picasso.get()
                .load(chat.photo)
                .placeholder(R.drawable.ic_group)
                .error(R.drawable.ic_group)
                .into(binding?.imageChat)
        }
        roleType = chat.roleType
        persons = chat.phones
        binding?.textDesc?.text = "${persons?.count()} подписчик(-a)"
    }

    private fun setListeners() {
        binding?.back?.setOnClickListener { onBackPress() }
        binding?.imageChat?.setOnClickListener { onMenuPress() }
        binding?.groupProfile?.setOnClickListener { onProfileGroup() }
    }

    private fun onProfileGroup() {
        val alertDialogGroupChatProfile = AlertDialogGroupChatProfile.newInstance(
            chatId!!, phone!!, binding?.txtName?.text.toString(), roleType, image
        )
        alertDialogGroupChatProfile.show(supportFragmentManager, "alertDialogGroupChatProfile")

    }

    private fun onMenuPress() {
        popupMenu = PopupMenu(this@ChatGroupActivity, binding!!.imageChat)
        popupMenu?.menuInflater?.inflate(R.menu.group_menu, popupMenu?.menu)
        isVisibleOnRoleType()
        popupMenu?.setOnMenuItemClickListener { onMenuItemClick(it) }
        popupMenu?.show()
    }

    private fun isVisibleOnRoleType() {
        when (roleType) {
            RoleTypes.User -> {
                menuItem(true)
            }

            RoleTypes.Admin -> {
                menuItem(false)
            }
        }
    }

    private fun menuItem(isTruth: Boolean) {
        popupMenu!!.menu.findItem(R.id.nav_exit_from_chat).isVisible = isTruth
        popupMenu!!.menu.findItem(R.id.nav_delete).isVisible = !isTruth
    }

    private fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_exit_from_chat -> {
                exitFromChat()
            }

            R.id.nav_delete -> {
                deletedMessage()

            }
        }
        return true
    }

    private fun exitFromChat() {
        chatViewModel.postLeaveGroup(chatId!!,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
    }

    private fun deletedMessage() {
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

    private fun removeListeners() {
        binding?.back?.setOnClickListener(null)
        binding?.imageChat?.setOnClickListener(null)
        binding?.groupProfile?.setOnClickListener(null)
        popupMenu?.setOnMenuItemClickListener(null)
    }

    private fun removeComponent() {
        popupMenu = null
        chatId = null
        phone = null
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
        chatViewModel.postJoinGroup(chatId!!,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ChatGroupActivity, "Ошибка! $message", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun getChatApi(api: ApiResponse<Chat>) {
        when (api) {
            is ApiResponse.Success -> {
                initChat(api.data)

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

    fun getUpdateChat(phone:String, chatId: UUID){
        fragment?.signalRViewModel?.updateChatInfo(phone, chatId)
    }
    private fun getChatJoin(api: ApiResponse<Chat>) {
        when (api) {
            is ApiResponse.Success -> {
                chatId = api.data.id
                fragment?.chatId = chatId!!
                fragment?.joinGroup(ChatTypes.Group)
                persons?.add(phone!!)
                binding?.imageChat?.visibility = View.VISIBLE
                for (person in persons!!){
                    fragment?.signalRViewModel?.updateChat(person)
                    getUpdateChat(person, chatId!!)
                }


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

    private fun getDeletedMessage(api: ApiResponse<ResponseBody>) {
        when (api) {
            is ApiResponse.Success -> {
                deletedChat()
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ChatGroupActivity, "Ошибка! ${api.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getLeaveChat(api: ApiResponse<Boolean>) {
        when (api) {
            is ApiResponse.Success -> {
                for (person in persons!!){
                    getUpdateChat(person, chatId!!)
                }
                fragment?.signalRViewModel?.exitChat(phone!!)
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ChatGroupActivity, "Ошибка! ${api.message}",
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
                for (person in persons!!){
                    fragment?.signalRViewModel?.exitChat(person)
                }
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@ChatGroupActivity, "Ошибка! ${api.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }
}
