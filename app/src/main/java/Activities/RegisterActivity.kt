package Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.klepetchat.databinding.RegisterBinding

class RegisterActivity : ComponentActivity() {
    private lateinit var binding : RegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
