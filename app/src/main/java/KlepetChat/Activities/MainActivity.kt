package KlepetChat.Activities

import KlepetChat.WebApi.Implementations.ViewModels.AuthViewModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.databinding.MainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var binding: MainBinding
    //private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.button.setOnClickListener{
//            var intent = Intent(this, AuthorizationActivity::class.java)
//            startActivity(intent)
//        }

    }
}
