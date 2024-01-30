package KlepetChat.Activities

import KlepetChat.RoomDB.Models.JWT
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.AuthViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.databinding.AuthorizationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthorizationActivity : ComponentActivity(){

    private lateinit var binding : AuthorizationBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.GetToken().observe(this) {
            when(it) {
                is ApiResponse.Failure -> binding.loginTest.text = it.message
                ApiResponse.Loading -> binding.loginTest.text = "Loading"
                is ApiResponse.Success -> {
                    var db = KlepetDB.getDB(this)
                    db.getDao().saveToken(JWT(phone = "efd",accessToken = it.data.accessToken, refreshToken = it.data.refreshToken))
                }
            }
        }
    }
}
