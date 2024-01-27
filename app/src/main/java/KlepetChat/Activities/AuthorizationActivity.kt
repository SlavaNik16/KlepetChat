package KlepetChat.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.klepetchat.databinding.AuthorizationBinding

class AuthorizationActivity : ComponentActivity(){

    private lateinit var binding : AuthorizationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
