package KlepetChat.Activities

import KlepetChat.DataSore.Models.UserData
import KlepetChat.WebApi.Implementations.Repositories.AuthRepository
import KlepetChat.WebApi.Implementations.ViewModels.AuthViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import KlepetChat.WebApi.Models.Request.Login
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klepetchat.databinding.AuthorizationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthorizationActivity : ComponentActivity() {

    private lateinit var binding: AuthorizationBinding
    //private val authViewModelTest: AuthViewModelTest by viewModels()
    private val authRepository: AuthRepository by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // Log.d("Test", "AuthViewModel = ${authViewModelTest}")
        Log.d("Test", "UserDataViewModel = ${userDataViewModel}")

        userDataViewModel.userData.observe(this) {
            if (it?.accessToken.toString().isNotBlank()) {
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        authRepository.getLoginObserve().observe(this) {
            if (it == null) {
                binding.loginTest.text = "Еrror"
                return@observe
            }
            userDataViewModel.SaveUserData(
                UserData(
                    binding.phoneField.text.toString(),
                    it.accessToken,
                    it.refreshToken
                )
            )
        }
        authRepository.getErrorObserve().observe(this) {
            Toast.makeText(this, "Код ${it.code} - ${it.message}",Toast.LENGTH_SHORT).show()
        }
//        authViewModelTest.token.observe(this) {
//            when (it) {
//                is ApiResponse.Failure -> binding.loginTest.text = it.message
//                ApiResponse.Loading -> binding.loginTest.text = "Loading"
//                is ApiResponse.Success -> {
//                    userDataViewModel.SaveUserData(
//                        UserData(
//                            binding.phoneField.text.toString(),
//                            it.data.accessToken ?: "",
//                            it.data.refreshToken ?: ""
//                        )
//                    )
//                }
//            }
//        }

        binding.butEnter.setOnClickListener {
                authRepository.Login(
                    Login(
                        binding.phoneField.text.toString(),
                        binding.passField.text.toString()
                    )
                )
//            authViewModelTest.loginTest(
//                Login(
//                    binding.phoneField.text.toString(),
//                    binding.passField.text.toString()
//                ),
//                object : ICoroutinesErrorHandler {
//                    override fun onError(message: String) {
//                        binding.loginTest.text = "Error! $message"
//                    }
//                }
//            )
        }
    }
}
