package KlepetChat.Activities.Chat

import ChatFragment
import KlepetChat.Activities.Data.Constants
import KlepetChat.Activities.MainActivity
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.MessageViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityChatFavoritesBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID


@AndroidEntryPoint
class ChatFavoritesActivity : AppCompatActivity() {
    private var binding: ActivityChatFavoritesBinding? = null
    private val chatViewModel: ChatViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()

    private var chatId: UUID? = null
    private var fragment: ChatFragment? = null
    private var popupMenu: PopupMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatFavoritesBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setListeners()
        init()

    }


    private fun fragmentInstance(f: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chatFragment, f)
            .commit()
    }


    private fun init() {
        val argument = intent.extras

        val chatIdStr = argument?.getString(Constants.KEY_CHAT_ID)
        chatId = UUID.fromString(chatIdStr)
        fragment = ChatFragment.newInstance(chatId!!)
        fragmentInstance(fragment!!)

        val txtName = argument?.getString(Constants.KEY_CHAT_NAME)
        binding?.txtName?.text = txtName

        binding?.imageChat?.setImageResource(R.drawable.favorites_icon)

    }

    private fun setListeners() {
        binding?.back?.setOnClickListener { onBackPress() }
        binding?.butMenu?.setOnClickListener { onMenuPress() }
    }

    private fun onMenuPress() {
        popupMenu = PopupMenu(this@ChatFavoritesActivity, binding!!.butMenu)
        popupMenu?.menuInflater?.inflate(R.menu.favorites_menu, popupMenu?.menu)

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

    private fun removeListeners() {
        binding?.back?.setOnClickListener(null)
        binding?.butMenu?.setOnClickListener(null)
        popupMenu?.setOnMenuItemClickListener(null)
    }
    private fun removeComponent() {
        popupMenu = null
        chatId = null
        fragment?.onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        removeComponent()
        this.viewModelStore.clear()
        binding = null
    }

    private fun onBackPress() {
        var intent = Intent(this@ChatFavoritesActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
