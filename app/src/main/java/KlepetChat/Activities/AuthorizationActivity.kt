package KlepetChat.Activities

import KlepetChat.DataSore.Models.UserData
import KlepetChat.WebApi.Implementations.ApiResponse

import KlepetChat.WebApi.Implementations.ViewModels.AuthViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Request.Login
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.databinding.AuthorizationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthorizationActivity : ComponentActivity() {

    private lateinit var binding: AuthorizationBinding
    private val authViewModel: AuthViewModel by viewModels()

    // private val authRepository: AuthRepository by viewModels()
    //private var authApiExit =  AuthApiExit()
    private val userDataViewModel: UserDataViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Log.d("Test", "AuthViewModel = ${authViewModelTest}")
        Log.d("Test", "UserDataViewModel = ${userDataViewModel}")

        userDataViewModel.userData.observe(this) {

            if (it?.accessToken.toString().isNotBlank()) {
                Log.d("POST", "AccessToken = ${it?.accessToken} и refreshToken = ${it?.refreshToken}")
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        authViewModel.token.observe(this) {
            when (it) {
                is ApiResponse.Failure -> binding.loginTest.text = it.message
                ApiResponse.Loading -> binding.loginTest.text = "Loading"
                is ApiResponse.Success -> {
                    userDataViewModel.SaveUserData(
                        UserData(
                            binding.phoneField.text.toString(),
                            it.data.accessToken ?: "",
                            it.data.refreshToken ?: ""
                        )
                    )
                }
            }
        }

       binding.butEnter.setOnClickListener {
           authViewModel.login(
               Login(
                   binding.phoneField.text.toString(),
                   binding.passField.text.toString()
               ),
               object : ICoroutinesErrorHandler {
                   override fun onError(message: String) {
                       Toast.makeText(it.context, "Пользователь не найден!", Toast.LENGTH_SHORT).show()
                   }
               }
           )
       }

    }
}
