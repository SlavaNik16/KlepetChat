package KlepetChat.Activities

import KlepetChat.DataSore.Context.DataStoreManager
import KlepetChat.DataSore.Models.UserData
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.AuthViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import KlepetChat.WebApi.Interfaces.IAuthService
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Request.Auth
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.databinding.AuthorizationBinding

class AuthorizationActivity : ComponentActivity(){

    private lateinit var binding : AuthorizationBinding
    private val authViewModel: AuthViewModel = AuthViewModel()
    private val userDataViewModel: UserDataViewModel = UserDataViewModel(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDataViewModel.userData.observe(this){
            if(it?.accessToken.toString().isNotBlank()){
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        authViewModel.GetToken().observe(this) {
            when(it) {
                is ApiResponse.Failure -> binding.loginTest.text = it.message
                ApiResponse.Loading -> binding.loginTest.text = "Loading"
                is ApiResponse.Success -> {
                    userDataViewModel.SaveUserData(
                        UserData(binding.phoneField.text.toString(),
                            it.data.accessToken,
                            it.data.refreshToken)
                    )
                }
            }
        }

        binding.butEnter.setOnClickListener {

            authViewModel.login(
                Auth(binding.phoneField.text.toString(),
                    binding.passField.text.toString()),
                object: ICoroutinesErrorHandler {
                    override fun onError(message: String) {
                        binding.loginTest.text = "Error! $message"
                    }
                }
            )
        }
    }
}
