package KlepetChat.Activities

import ChatFragment
import KlepetChat.Activities.Data.Constants
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityChatFavoritesBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID


@AndroidEntryPoint
class ChatFavoritesActivity : AppCompatActivity() {
    private var binding: ActivityChatFavoritesBinding? = null

    private lateinit var chatId: UUID
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
        val fragment = ChatFragment.newInstance(chatId)
        fragmentInstance(fragment)

        val txtName = argument?.getString(Constants.KEY_CHAT_NAME)
        binding?.txtName?.text = txtName

        binding?.imageChat?.setImageResource(R.drawable.favorites_icon)

    }

    private fun setListeners() {
        binding?.back?.setOnClickListener { onBackPress() }
    }

    private fun removeListeners() {
        binding?.back?.setOnClickListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        binding = null
    }

    private fun onBackPress() {
        var intent = Intent(this@ChatFavoritesActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
