package KlepetChat.Activities

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Request.UserRegister
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : ComponentActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private val  userViewModel: UserViewModel by viewModels()
    private val  chatViewModel: ChatViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel.user.observe(this) {
            when (it) {
                is ApiResponse.Failure ->
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                ApiResponse.Loading ->
                    Toast.makeText(this,
                        "Пожалуйста подождите!",Toast.LENGTH_SHORT ).show()
                is ApiResponse.Success -> {
                    Toast.makeText(this,
                        "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show()
                    chatViewModel.postFavorites(it.data.id,
                        object : ICoroutinesErrorHandler {
                            override fun onError(message: String) {
                                Toast.makeText(applicationContext,
                                    "Ошибка! ${message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    var intent = Intent(this, AuthorizationActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.butReg.setOnClickListener {
            var password = binding.passwordField
            if(password.length() < 8){
                Toast.makeText(it.context, "Слишком маленький пароль (не меньше 8)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            userViewModel.postCreate(
                UserRegister(
                    binding.surnameField.text.toString(),
                    binding.nameField.text.toString(),
                    binding.phoneField.text.toString(),
                    password.text.toString()
                ),
                object : ICoroutinesErrorHandler {
                    override fun onError(message: String) {
                        Toast.makeText(it.context, "Ошибка! ${message}", Toast.LENGTH_SHORT).show()
                    }
                }


            )

        }
    }
}
