package KlepetChat.Activities

import KlepetChat.Activities.Chat.ChatContactActivity
import KlepetChat.Activities.Chat.ChatFavoritesActivity
import KlepetChat.Activities.Chat.ChatGroupActivity
import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.ChatViewItemAdapter
import KlepetChat.DataSore.Models.UserData
import KlepetChat.Utils.NotificationUtils
import KlepetChat.Utils.TextChangedListener
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.SignalR.SignalRViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import KlepetChat.WebApi.Models.Response.User
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityMainBinding
import com.example.klepetchat.databinding.NavHeaderBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var bindingHeader: NavHeaderBinding? = null
    private val signalRViewModel: SignalRViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var adapter: RecyclerView.Adapter<ChatViewItemAdapter.ChatViewItemHolder>
    private var isEdit = false
    private lateinit var chats: MutableList<Chat>
    private lateinit var user: User
    private var notificationUtils:NotificationUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        var viewHeader = binding?.navigationView!!.inflateHeaderView(R.layout.nav_header)
        notificationUtils = NotificationUtils().getInstance(this)
        bindingHeader = NavHeaderBinding.bind(viewHeader)
        setContentView(binding?.root)
        CheckPermission()
        registerNotification()
        signalRViewModel.getConnection().on("AnswerNotification", {
            runOnUiThread(Runnable {
                sendNotificationCreate(it)
            })
        },Chat::class.java)

        signalRViewModel.start()
        setListeners()
        setObserve()
        initDrawLayout()
        loading(true)
    }

    private fun CheckPermission(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            AddPermission()
            return
        }
    }
    private fun registerNotification(){
        notificationUtils?.registerNotification()
    }
    private fun sendNotificationCreate(chat:Chat){
        val intent = Intent(this, ChatContactActivity::class.java).apply {
            this.putExtra(Constants.KEY_CHAT_ID, chat.id.toString())
            this.putExtra(Constants.KEY_CHAT_NAME, chat.name)
            this.putExtra(Constants.KEY_IMAGE_URL, chat.photo)
            this.putExtra(Constants.KEY_USER_PHONE_OTHER, chat.phones[0])
        }.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        notificationUtils?.sendNotificationCreate(chat.name + " написал тебе: ", chat.lastMessage!!, pendingIntent)
    }
    private fun AddPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(
                Manifest.permission.POST_NOTIFICATIONS
            ),
            Constants.REQUEST_PERMISSION_POST_NOTIFICATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_PERMISSION_POST_NOTIFICATION ->
                if (grantResults.isNotEmpty()
                && grantResults[0] === PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this@MainActivity, "Уведомления включены!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Уведомления отключены!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setObserve() {
        chatViewModel.chats.observe(this) { getChats(it) }
        userViewModel.user.observe(this) { getUser(it) }
        userDataViewModel.userData.observe(this) { validateUser(it) }
        chatViewModel.exists.observe(this) { getAnswerCreate(it) }
    }

    private fun getAnswerCreate(api: ApiResponse<Boolean>) {
        when (api) {
            is ApiResponse.Success -> {
                Toast.makeText(this@MainActivity, "Чат успешно создан!", Toast.LENGTH_SHORT).show()
                var intent = intent
                finish()
                startActivity(intent)
            }

            is ApiResponse.Failure -> {
                var chat = chats.firstOrNull { x -> x.name == "Избранное" } ?: return
                navigateToFavorites(chat)
            }


            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getChats(api: ApiResponse<MutableList<Chat>>) {
        when (api) {
            is ApiResponse.Success -> {
                this.chats = api.data
                adapter = ChatViewItemAdapter(chats)
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
        binding?.imageSearch?.setOnClickListener(null)
        binding?.inputSearch?.addTextChangedListener(null)
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
        binding?.imageSearch?.setOnClickListener { setSearchChat() }
        binding?.inputSearch?.addTextChangedListener(addTextSearchChange())

    }

    private fun addTextSearchChange(): TextWatcher {
        return object : TextChangedListener<EditText>(binding?.inputSearch!!) {
            private var timer = Timer()
            private val DELAY: Long = 500
            override fun onTextChanged(target: EditText, s: Editable?) {
                if (target.text.isNullOrBlank()) {
                    if (isEdit) {
                        getChats()
                    }
                    return
                }
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                resultTextSearch()
                            }
                        }
                    },
                    DELAY
                )
            }

        }
    }

    private fun resultTextSearch() {
        isEdit = true
        chatViewModel.getChatsByName(binding?.inputSearch?.text.toString(),
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@MainActivity, "Error! ${message}\n", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setSearchChat() {
        var modeTag = binding?.imageSearch?.tag.toString()
        if (modeTag == Constants.KEY_TAG_SEARCH) {
            isEdit = false
            binding?.imageSearch?.tag = Constants.KEY_TAG_SEARCHOFF
            binding?.imageSearch?.setImageResource(R.drawable.ic_close_white)
            binding?.inputSearch?.visibility = View.VISIBLE
        } else {
            binding?.imageSearch?.tag = Constants.KEY_TAG_SEARCH
            binding?.imageSearch?.setImageResource(R.drawable.ic_search)
            binding?.inputSearch?.visibility = View.GONE
            binding?.inputSearch?.setText(String())
            if (getSystemService(Context.INPUT_METHOD_SERVICE) is InputMethodManager) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
            }
        }
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
            R.id.nav_add_group -> onAddChat(true)
            R.id.nav_add_contact -> onAddChat()
            R.id.nav_add_favorites -> createFavorites()
            R.id.nav_settings -> navigateToProfile()
            R.id.nav_help -> onHelp()
            R.id.nav_exit -> exitAuth()
        }
        return true
    }

    private fun onHelp() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.org/faq"))
        startActivity(browserIntent)
    }

    private fun createFavorites() {
        chatViewModel.postFavorites(user.id, object : ICoroutinesErrorHandler {
            override fun onError(message: String) {

            }
        })
    }

    private fun navigateToFavorites(chat: Chat) {
        val intent =
            Intent(this@MainActivity, ChatFavoritesActivity::class.java)
        intent.putExtra(Constants.KEY_CHAT_ID, chat.id.toString())
        intent.putExtra(Constants.KEY_CHAT_NAME, chat.name)
        startActivity(intent)
        finish()
    }

    private fun navigateToContact(chat: Chat) {
        val intent = Intent(this@MainActivity, ChatContactActivity::class.java)
        intent.putExtra(Constants.KEY_CHAT_ID, chat.id.toString())
        intent.putExtra(Constants.KEY_CHAT_NAME, chat.name)
        intent.putExtra(Constants.KEY_IMAGE_URL, chat.photo)
        intent.putExtra(Constants.KEY_USER_PHONE_OTHER, chat.phones[0])
        startActivity(intent)
        finish()
    }

    private fun navigateToGroup(chat: Chat) {
        val intent = Intent(this@MainActivity, ChatGroupActivity::class.java)
        intent.putExtra(Constants.KEY_CHAT_ID, chat.id.toString())
        intent.putExtra(Constants.KEY_CHAT_NAME, chat.name)
        intent.putExtra(Constants.KEY_IMAGE_URL, chat.photo)
        var arrayList: ArrayList<String> = arrayListOf()
        for (item in chat.phones) {
            arrayList.add(item)
        }
        intent.putStringArrayListExtra(Constants.KEY_CHAT_PEOPLE, arrayList)
        intent.putExtra(Constants.KEY_USER_PHONE, user.phone)
        intent.putExtra(Constants.KEY_USER_ROLE, chat.roleType.name)
        startActivity(intent)
        finish()
    }

    private fun onRecyclerAttachState(): RecyclerView.OnChildAttachStateChangeListener {
        return object : RecyclerView.OnChildAttachStateChangeListener {

            override fun onChildViewAttachedToWindow(view: View) {
                var position =
                    binding?.recyclerChat?.findContainingViewHolder(view)!!.adapterPosition
                view.findViewById<LinearLayout>(R.id.Chat).setOnClickListener {
                    var chat = this@MainActivity.chats[position]
                    when (chat.chatType) {
                        ChatTypes.Contact -> {
                            navigateToContact(chat)
                        }

                        ChatTypes.Favorites -> {
                            navigateToFavorites(chat)
                        }

                        ChatTypes.Group -> {
                            navigateToGroup(chat)
                        }
                    }
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                return
            }
        }
    }

    private fun onAddChat(isOpenGroup: Boolean = false) {
        val intent = Intent(this@MainActivity, ChooseActivity::class.java)
        intent.putExtra(Constants.KEY_IS_OPEN_GROUP, isOpenGroup)
        startActivity(intent)
        finish()
    }

    private fun navigateToProfile() {
        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
        intent.putExtra(Constants.KEY_PROFILE_VIEW, false)
        intent.putExtra(Constants.KEY_USER_PHONE, user.phone)
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
