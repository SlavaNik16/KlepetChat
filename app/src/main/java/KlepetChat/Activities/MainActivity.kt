package KlepetChat.Activities

import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.ChatViewItemAdapter
import KlepetChat.DataSore.Models.UserData
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.User
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityMainBinding
import com.example.klepetchat.databinding.NavHeaderBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var bindingHeader: NavHeaderBinding? = null

    private val userViewModel: UserViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var adapter: RecyclerView.Adapter<ChatViewItemAdapter.ChatViewItemHolder>

    private lateinit var chats: MutableList<Chat>
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        var viewHeader = binding?.navigationView!!.inflateHeaderView(R.layout.nav_header)
        bindingHeader = NavHeaderBinding.bind(viewHeader)
        setContentView(binding?.root)
        setListeners()
        setObserve()
        initDrawLayout()
        loading(true)
    }

    private fun setObserve() {
        chatViewModel.chats.observe(this) { getChats(it) }
        userViewModel.user.observe(this) { getUser(it) }
        userDataViewModel.userData.observe(this) { validateUser(it) }
    }

    private fun getChats(api: ApiResponse<MutableList<Chat>>) {
        when (api) {
            is ApiResponse.Success -> {
                this.chats = api.data
                adapter = ChatViewItemAdapter(this, chats)
                binding?.recyclerChat?.adapter = adapter
                loading(false)
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@MainActivity, "Ошибка! ${api.message}", Toast.LENGTH_SHORT
                ).show()
                exitAuth()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getUser(api: ApiResponse<User>) {
        when (api) {
            is ApiResponse.Success -> {
                user = api.data
                initNavigationViewHeader(user)
                getChats()
            }

            is ApiResponse.Failure -> {
                Toast.makeText(
                    this@MainActivity, "Ошибка! $api.message", Toast.LENGTH_SHORT
                ).show()
                exitAuth()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getChats() {
        chatViewModel.getChats(
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        applicationContext,
                        "Ошибка! $message", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun validateUser(userData: UserData?) {
        if (userData?.phone.isNullOrBlank() || userData?.accessToken.isNullOrBlank()) {
            exitAuth()
            return
        }
        getByPhone(userData!!.phone)
    }

    private fun getByPhone(phone: String) {
        userViewModel.getByPhone(phone,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@MainActivity, "Error! ${message}\n", Toast.LENGTH_SHORT
                    )
                        .show()
                    exitAuth()
                }
            })
    }

    private fun initDrawLayout() {
        setSupportActionBar(binding?.toolBar)
        var toggle = ActionBarDrawerToggle(
            this,
            binding?.drawerLayout,
            binding?.toolBar,
            R.string.open,
            R.string.close
        )
        binding?.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        binding = null
        bindingHeader = null
    }

    private fun removeListeners() {
        binding?.butAddChat?.setOnClickListener(null)
        binding?.navigationView?.setNavigationItemSelectedListener(null)
        bindingHeader?.imageMode?.setOnClickListener(null)
        chats.clear()
        binding?.recyclerChat?.adapter = null
        binding?.recyclerChat?.layoutManager = null
        binding?.recyclerChat?.recycledViewPool?.clear()
    }

    private fun setListeners() {
        binding?.butAddChat?.setOnClickListener { onAddChat() }
        binding?.recyclerChat?.addOnChildAttachStateChangeListener(onRecyclerAttachState())
        binding?.navigationView?.setNavigationItemSelectedListener { setMenuItem(it) }
        bindingHeader?.imageMode?.setOnClickListener { setMode() }
    }

    private fun setMode() {
        var modeTag = bindingHeader?.imageMode?.tag.toString()
        if (modeTag == Constants.KEY_TAG_MOON) {
            bindingHeader?.imageMode?.setImageResource(R.drawable.ic_sun)
            bindingHeader?.imageMode?.tag = Constants.KEY_TAG_SUN
        } else {
            bindingHeader?.imageMode?.setImageResource(R.drawable.ic_moon)
            bindingHeader?.imageMode?.tag = Constants.KEY_TAG_MOON
        }
    }

    private fun setMenuItem(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_add_group -> {
                Toast.makeText(this@MainActivity, "nav_add_group", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_add_contact -> {
                Toast.makeText(this@MainActivity, "nav_add_contact", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_settings -> navigateToProfile()
            R.id.nav_help -> {
                Toast.makeText(this@MainActivity, "nav_help", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_exit -> {
                exitAuth()
            }
        }
        return true
    }

    private fun onRecyclerAttachState(): RecyclerView.OnChildAttachStateChangeListener {
        return object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                var position =
                    binding?.recyclerChat?.findContainingViewHolder(view)!!.adapterPosition
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
                    finish()
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
        finish()
    }

    private fun navigateToProfile() {
        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initNavigationViewHeader(user: User) {
        bindingHeader?.textFIO?.text = "${user.surname} ${user.name}"
        bindingHeader?.textPhone?.text = user.phone
        if (user.photo.isNullOrBlank()) {
            return
        }
        Picasso.get()
            .load(user.photo)
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .into(bindingHeader?.imageUser)
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding?.progressBar?.visibility = View.VISIBLE
        } else {
            binding?.progressBar?.visibility = View.INVISIBLE
        }
    }

    private fun exitAuth() {
        userDataViewModel.ClearUserData()
        var intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
