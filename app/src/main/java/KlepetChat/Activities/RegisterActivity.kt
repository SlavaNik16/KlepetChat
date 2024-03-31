package KlepetChat.Activities

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.TokenViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Request.UserRegister
import KlepetChat.WebApi.Models.Response.User
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class RegisterActivity : ComponentActivity() {
    private var binding: ActivityRegisterBinding? = null
    private val userViewModel: UserViewModel by viewModels()
    private val tokenViewModel: TokenViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setObserve()
        setListeners();
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        binding = null
    }

    private fun setListeners() {
        binding?.butReg?.setOnClickListener { onRegister() }
        binding?.butBack?.setOnClickListener { navigateToAuthorization() }
    }

    private fun removeListeners() {
        binding?.butReg?.setOnClickListener(null)
        binding?.butBack?.setOnClickListener(null)
    }

    private fun setObserve() {
        userViewModel.user.observe(this) { createUser(it) }
    }

    private fun createUser(api: ApiResponse<User>) {
        when (api) {
            is ApiResponse.Failure ->
                Toast.makeText(this, api.message, Toast.LENGTH_SHORT).show()

            ApiResponse.Loading ->
                Toast.makeText(
                    this,
                    "Пожалуйста подождите!", Toast.LENGTH_SHORT
                ).show()

            is ApiResponse.Success -> {
                Toast.makeText(
                    this,
                    "Регистрация прошла успешно!", Toast.LENGTH_SHORT
                ).show()
                postToken(api.data.phone)
                postFavorites(api.data.id)
                navigateToAuthorization()
            }
        }
    }

    private fun postToken(phone: String) {
        tokenViewModel.postCreate(phone,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        applicationContext, "Ошибка! $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun onRegister() {
        var password = binding!!.passwordField
        var phone = binding!!.phoneField;
        if (password.length() < 8) {
            Toast.makeText(
                applicationContext, "Слишком маленький пароль (не меньше 8)",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (phone.text!!.length < 11) {
            Toast.makeText(
                applicationContext, "Такого номера телефона не существует!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        userViewModel.postCreate(
            UserRegister(
                binding?.surnameField?.text.toString(),
                binding?.nameField?.text.toString(),
                binding?.phoneField?.text.toString(),
                password.text.toString()
            ),
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        applicationContext, "Ошибка! $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun postFavorites(userId: UUID) {
        chatViewModel.postFavorites(userId,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        applicationContext,
                        "Ошибка! $message", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun navigateToAuthorization() {
        var intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
