package KlepetChat.Activities

import KlepetChat.DataSore.Models.UserData
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.databinding.MainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var binding: MainBinding

    private val userViewModel: UserViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel.user.observe(this){
            when(it) {
                is ApiResponse.Success -> {
                    Toast.makeText(
                        this, "Добро пожаловать!\n" +
                                "${it.data.surname} ${it.data.name}", Toast.LENGTH_SHORT
                    ).show()
                }
                is ApiResponse.Failure -> {
                    Toast.makeText(
                        this@MainActivity, "Проверка! Войдите еще раз в аккаунт", Toast.LENGTH_SHORT)
                        .show()
                }
                is ApiResponse.Loading -> {

                }
            }
        }

        userDataViewModel.userData.observe(this){
            if((it!!.accessToken ?: "") == ""){
                var intent = Intent(this@MainActivity, AuthorizationActivity::class.java)
                startActivity(intent)
            }
            userViewModel.getByPhone(it.phone, object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@MainActivity, "Error! ${message}\n", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }
}
