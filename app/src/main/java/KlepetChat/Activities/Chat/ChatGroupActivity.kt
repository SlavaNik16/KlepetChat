package KlepetChat.Activities.Chat

import ChatFragment
import KlepetChat.Activities.Data.Constants
import KlepetChat.Activities.DialogFragment.AlertDialogGroupChatProfile
import KlepetChat.Activities.MainActivity
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import KlepetChat.WebApi.Models.Response.Enums.RoleTypes
import android.content.Intent
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
import java.util.UUID


@AndroidEntryPoint
class ChatGroupActivity : AppCompatActivity() {
    var binding: ActivityChatGroupBinding? = null

    private val chatViewModel: ChatViewModel by viewModels()

    private var chatId: UUID? = null
    private var phone: String? = null
    private lateinit var roleType: RoleTypes

    private lateinit var fragment: ChatFragment
    private var popupMenu: PopupMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatGroupBinding.inflate(layoutInflater)
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

        val persons = argument?.getStringArrayList(Constants.KEY_CHAT_PEOPLE)
        binding?.textDesc?.text = "${persons?.count()} подписчик(-a)"
        phone = argument?.getString(Constants.KEY_USER_PHONE)
        val chatIdStr = argument?.getString(Constants.KEY_CHAT_ID)
        chatId = UUID.fromString(chatIdStr)
        if (persons!!.contains(phone)) {
            fragment = ChatFragment.newInstance(chatId!!, ChatTypes.Group)
            fragmentInstance(fragment)
            binding?.butMenu?.visibility = View.VISIBLE
        } else {
            fragment = ChatFragment.newInstanceInit() { onInitChat() }
            fragmentInstance(fragment)
            binding?.butMenu?.visibility = View.INVISIBLE
        }

        val txtName = argument.getString(Constants.KEY_CHAT_NAME)
        binding?.txtName?.text = txtName

        var roleTypeStr = argument.getString(Constants.KEY_USER_ROLE).toString()
        roleType = when(roleTypeStr){
            RoleTypes.User.name -> RoleTypes.User
            RoleTypes.Admin.name ->  RoleTypes.Admin
            else -> RoleTypes.User
        }

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
        binding?.butMenu?.setOnClickListener { onMenuPress() }
        binding?.groupProfile?.setOnClickListener{ onProfileGroup() }
    }

    private fun onProfileGroup() {
        var image = intent?.extras?.getString(Constants.KEY_IMAGE_URL)
        val alertDialogGroupChatProfile = AlertDialogGroupChatProfile.newInstance(
            chatId!!, phone!!, binding?.txtName?.text.toString(), roleType, image)
        alertDialogGroupChatProfile.show(supportFragmentManager, "alertDialogGroupChatProfile")

    }

    private fun onMenuPress() {
        popupMenu = PopupMenu(this@ChatGroupActivity, binding!!.butMenu)
        popupMenu?.menuInflater?.inflate(R.menu.group_menu, popupMenu?.menu)
        isVisibleOnRoleType()
        popupMenu?.setOnMenuItemClickListener { onMenuItemClick(it) }
        popupMenu?.show()
    }

    private fun isVisibleOnRoleType(){
        when(roleType){
            RoleTypes.User -> {
                menuItem(true)
            }
            RoleTypes.Admin ->{
                menuItem(false)
            }
        }
    }
    private fun menuItem(isTruth:Boolean){
        popupMenu!!.menu.findItem(R.id.nav_exit_from_chat).isVisible = isTruth
        popupMenu!!.menu.findItem(R.id.nav_delete).isVisible = !isTruth
    }
    private fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_exit_from_chat -> {
                exitFromChat()
            }

            R.id.nav_delete -> {
                deletedChat()
            }
        }
        return true
    }

    private fun exitFromChat(){
        chatViewModel.postLeaveGroup(chatId!!,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
        onBackPress()
    }
    private fun deletedChat() {
        chatViewModel.deleteChat(chatId!!,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {

                }
            })
        onBackPress()
    }

    private fun removeListeners() {
        binding?.back?.setOnClickListener(null)
        binding?.butMenu?.setOnClickListener(null)
        binding?.groupProfile?.setOnClickListener(null)
        popupMenu?.setOnMenuItemClickListener(null)
    }
    private fun removeComponent(){
        popupMenu = null
        chatId = null
        phone = null
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        removeComponent()
        fragment.onDestroy()
        binding = null
    }

    override fun onPause() {
        super.onPause()
        fragment.leaveGroup()
    }

    private fun onBackPress() {
        var intent = Intent(this@ChatGroupActivity, MainActivity::class.java)
        startActivity(intent)
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

    private fun getChat(api: ApiResponse<Chat>) {
        when (api) {
            is ApiResponse.Success -> {
                chatId = api.data.id
                fragment.chatId = chatId!!
                fragment.joinGroup()
                binding?.butMenu?.visibility = View.VISIBLE
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
