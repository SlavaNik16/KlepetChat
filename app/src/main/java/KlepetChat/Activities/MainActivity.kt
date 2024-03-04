package KlepetChat.Activities

import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.ChatViewItemAdapter
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.User
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawLayout: DrawerLayout

    private val userViewModel: UserViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var adapter: RecyclerView.Adapter<ChatViewItemAdapter.ChatViewItemHolder>

    private lateinit var chats: MutableList<Chat>
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initDrawLayout()
        setContentView(binding.root)
        loading(true)
        setListeners()

        chatViewModel.chats.observe(this) {
            when (it) {
                is ApiResponse.Success -> {
                    this.chats = it.data
                    adapter = ChatViewItemAdapter(this, chats)
                    binding.recyclerChat.adapter = adapter
                    loading(false)
                }

                is ApiResponse.Failure -> {
                    Toast.makeText(
                        this@MainActivity, "Ошибка! ${it.message}", Toast.LENGTH_SHORT
                    )
                        .show()
                    exitAuth()
                }

                is ApiResponse.Loading -> {
                    return@observe
                }
            }
        }

        userViewModel.user.observe(this) {
            when (it) {
                is ApiResponse.Success -> {
                    user = it.data
                    chatViewModel.getChats(
                        object : ICoroutinesErrorHandler {
                            override fun onError(message: String) {
                                Toast.makeText(
                                    applicationContext,
                                    "Ошибка! ${message}", Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }

                is ApiResponse.Failure -> {
                    Toast.makeText(
                        this@MainActivity, "Ошибка! ${it.message}", Toast.LENGTH_SHORT
                    )
                        .show()
                    exitAuth()
                }

                is ApiResponse.Loading -> {
                    return@observe
                }
            }
        }

        userDataViewModel.userData.observe(this) {
            if ((it!!.accessToken ?: "") == "") {
                var intent = Intent(this@MainActivity, AuthorizationActivity::class.java)
                startActivity(intent)
            }
            userViewModel.getByPhone(it.phone, object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@MainActivity, "Error! ${message}\n", Toast.LENGTH_SHORT
                    )
                        .show()
                    exitAuth()
                }
            })
        }


    }

    private fun initDrawLayout() {
        setSupportActionBar(binding.toolBar)
        drawLayout = binding.drawerLayout
        var toggle = ActionBarDrawerToggle(this, drawLayout, binding.toolBar, R.string.open, R.string.close)
        drawLayout.addDrawerListener(toggle)
        toggle.syncState()
    }


    private fun setListeners() {
        binding.butAddChat.setOnClickListener { onAddChat() }
        binding.recyclerChat.addOnChildAttachStateChangeListener(onRecyclerAttachState())
        binding.navigationView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.nav_add_group -> {
                    Toast.makeText(this@MainActivity, "nav_add_group", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_add_contact -> {
                    Toast.makeText(this@MainActivity, "nav_add_contact", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> {
                    Toast.makeText(this@MainActivity, "nav_settings", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_help -> {
                    Toast.makeText(this@MainActivity, "nav_help", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_exit -> {
                    Toast.makeText(this@MainActivity, "nav_exit", Toast.LENGTH_SHORT).show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun onRecyclerAttachState(): RecyclerView.OnChildAttachStateChangeListener {
        return object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                var position =
                    binding.recyclerChat.findContainingViewHolder(view)!!.adapterPosition
                view.findViewById<LinearLayout>(R.id.Chat).setOnClickListener {
                    var chat = this@MainActivity.chats[position]
                    val intent = Intent(this@MainActivity, ChatActivity::class.java)

                    intent.putExtra(Constants.KEY_CHAT_ID, chat.id.toString())
                    intent.putExtra(Constants.KEY_CHAT_NAME, chat.name)
                    intent.putExtra(Constants.KEY_IMAGE_URL, chat.photo)
                    intent.putExtra(Constants.KEY_USER_PHONE, user.phone)
                    intent.putExtra(Constants.KEY_CHAT_TYPE, chat.chatType.name)
                    intent.putExtra(Constants.KEY_IS_PREV, false)
                    startActivity(intent)
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                return
            }
        }
    }

    private fun onAddChat() {
        val intent = Intent(this@MainActivity, ChooseActivity::class.java)
        startActivity(intent)
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun exitAuth() {
        userDataViewModel.ClearUserData()
    }
}
