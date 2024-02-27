package KlepetChat.Activities

import KlepetChat.Activities.Data.Constants
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.klepetchat.databinding.ChatBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatActivity : ComponentActivity() {
    private lateinit var binding: ChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadDetails()
        binding.back.setOnClickListener {
            var intent = Intent(this@ChatActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadDetails() {
        var argument = intent.extras
        var txtName = argument?.getString(Constants.KEY_CHAT_NAME)
        binding.txtName.text = txtName
        //Picasso.get().load() binding.imageChat
    }


}
