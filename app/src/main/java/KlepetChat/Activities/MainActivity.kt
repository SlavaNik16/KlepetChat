package KlepetChat.Activities

import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.databinding.MainBinding


class MainActivity : ComponentActivity() {
    private lateinit var binding: MainBinding
    private val userDataViewModel: UserDataViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDataViewModel.userData.observe(this){
            if(it == null) return@observe

            Toast.makeText(this, "Добро пожаловать:\n", Toast.LENGTH_SHORT).show()
        }
    }
}
