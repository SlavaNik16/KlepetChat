package KlepetChat.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.klepetchat.databinding.RegisterBinding
import dagger.hilt.android.AndroidEntryPoint

class RegisterActivity : ComponentActivity() {
    private lateinit var binding : RegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
