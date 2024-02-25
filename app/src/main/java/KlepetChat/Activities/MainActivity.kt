package KlepetChat.Activities

import KlepetChat.Adapters.ChatViewItemAdapter
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Chat
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import com.example.klepetchat.R
import com.example.klepetchat.databinding.MainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var binding: MainBinding

    private val userViewModel: UserViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var adapter: RecyclerView.Adapter<ChatViewItemAdapter.ChatViewItemHolder>
    private lateinit var chats: MutableList<Chat>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatViewModel.chats.observe(this) {
            when (it) {
                is ApiResponse.Success -> {
                    var layoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                    binding.recyclerChat.layoutManager = layoutManager
                    this.chats = it.data
                    adapter = ChatViewItemAdapter(this, chats)
                    binding.recyclerChat.adapter = adapter
                }

                is ApiResponse.Failure -> {
                    Toast.makeText(
                        this@MainActivity, "Ошибка! ${it.message}", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                is ApiResponse.Loading -> {

                }
            }
        }

        userViewModel.user.observe(this){
            when(it) {
                is ApiResponse.Success -> {
                    Toast.makeText(
                        this, "Добро пожаловать!\n" +
                                "${it.data.surname} ${it.data.name}", Toast.LENGTH_SHORT
                    ).show()
                    chatViewModel.getChats(
                        object : ICoroutinesErrorHandler {
                            override fun onError(message: String) {
                                Toast.makeText(applicationContext,
                                    "Ошибка! ${message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
                is ApiResponse.Failure -> {
                    Toast.makeText(
                        this@MainActivity, "Ошибка! ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }
                is ApiResponse.Loading -> {
                }
            }
        }

        userDataViewModel.userData.observe(this){
            if((it!!.accessToken ?: "") == ""){
                var intent = Intent(this@MainActivity, AuthorizationActivity::class.java)
                startActivity(intent)
            }
            userViewModel.getByPhone(it.phone, object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@MainActivity, "Error! ${message}\n", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
        binding.recyclerChat.addOnChildAttachStateChangeListener(
            object : OnChildAttachStateChangeListener {
                override fun onChildViewAttachedToWindow(view: View) {
                    var position = binding.recyclerChat.findContainingViewHolder(view)!!.adapterPosition
                    view.findViewById<LinearLayout>(R.id.Chat).setOnClickListener{
                        Toast.makeText(view.context, "ToWindow ${position}",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onChildViewDetachedFromWindow(view: View) {
                    Toast.makeText(view.context, "FromWindow",Toast.LENGTH_SHORT).show()
                }
            })
    }
}
