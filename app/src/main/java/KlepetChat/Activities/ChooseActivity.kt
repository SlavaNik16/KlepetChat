package KlepetChat.Activities

import KlepetChat.Activities.Data.Constants
import KlepetChat.Adapters.UserViewItemAdapter
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.Enums.ChatTypes
import KlepetChat.WebApi.Models.Response.User
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityChooseBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChooseActivity : ComponentActivity() {
    private lateinit var binding: ActivityChooseBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var adapter: RecyclerView.Adapter<UserViewItemAdapter.UserViewItemHolder>
    private lateinit var users: MutableList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel.users.observe(this){
            when (it) {
                is ApiResponse.Success -> {
                    this.users = it.data
                    adapter = UserViewItemAdapter(this, users)
                    binding.contactRecycler.adapter = adapter
                }

                is ApiResponse.Failure -> {
                    Toast.makeText(
                        this@ChooseActivity, "Ошибка! ${it.message}", Toast.LENGTH_SHORT
                    ).show()
                }

                is ApiResponse.Loading -> {
                    return@observe
                }
            }
        }

        userViewModel.getContactsOther(object : ICoroutinesErrorHandler {
            override fun onError(message: String) {
                Toast.makeText(
                    this@ChooseActivity, "Ошибка! ${message}", Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding.contactRecycler.addOnChildAttachStateChangeListener(
            object : RecyclerView.OnChildAttachStateChangeListener {
                override fun onChildViewAttachedToWindow(view: View) {
                    var position =
                        binding.contactRecycler.findContainingViewHolder(view)!!.adapterPosition
                    view.findViewById<LinearLayout>(R.id.Chat).setOnClickListener {
                        var user = this@ChooseActivity.users[position]
                        val intent = Intent(this@ChooseActivity, ChatActivity::class.java)
                        intent.putExtra(Constants.KEY_USER_PHONE, user.phone)
                        intent.putExtra(Constants.KEY_CHAT_NAME, user.name)
                        intent.putExtra(Constants.KEY_IMAGE_URL, user.photo)
                        intent.putExtra(Constants.KEY_CHAT_TYPE, ChatTypes.Contact)
                        intent.putExtra(Constants.KEY_IS_PREV, true)
                        startActivity(intent)
                    }
                }

                override fun onChildViewDetachedFromWindow(view: View) {
                    return
                }
            })
        binding.back.setOnClickListener {
            var intent = Intent(this@ChooseActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}