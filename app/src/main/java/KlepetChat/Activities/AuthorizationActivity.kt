package KlepetChat.Activities

import KlepetChat.DataSore.Models.UserData
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.AuthViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Request.Login
import KlepetChat.WebApi.Models.Response.Token
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.databinding.AuthorizationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthorizationActivity : ComponentActivity() {

    private var binding: AuthorizationBinding? = null
    private val authViewModel: AuthViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthorizationBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setListeners()
        setObserve()
    }

    private fun setObserve() {
        authViewModel.token.observe(this) { saveUserData(it) }
    }

    private fun saveUserData(api: ApiResponse<Token>) {
        when (api) {
            is ApiResponse.Failure -> Toast.makeText(
                this, api.message,
                Toast.LENGTH_SHORT
            ).show()

            ApiResponse.Loading -> Toast.makeText(
                this,
                "Пожалуйста подождите!", Toast.LENGTH_SHORT
            ).show()

            is ApiResponse.Success -> {
                userDataViewModel.SaveUserData(
                    UserData(
                        binding?.phoneField?.text.toString(),
                        api.data.accessToken ?: "",
                        api.data.refreshToken ?: "",
                        false
                    )
                )
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
        binding = null
    }

    private fun removeListeners() {
        binding?.txtButRegister?.setOnClickListener(null)
        binding?.butEnter?.setOnClickListener(null)
    }

    private fun setListeners() {
        binding?.txtButRegister?.setOnClickListener { navigateToRegister() }
        binding?.butEnter?.setOnClickListener { login() }
    }

    private fun login() {
        var password = binding!!.passField
        var phone = binding!!.phoneField;
        if (password.length() < 8) {
            Toast.makeText(
                applicationContext, "Слишком маленький пароль (не меньше 8)", Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (phone.text!!.length < 11) {
            Toast.makeText(
                applicationContext, "Такого номера телефона не существует!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        authViewModel.login(Login(
            binding?.phoneField?.text.toString(), binding?.passField?.text.toString()
        ), object : ICoroutinesErrorHandler {
            override fun onError(message: String) {

            }
        })
    }

    private fun navigateToRegister() {
        var intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}
