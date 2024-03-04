package KlepetChat.Activities

import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.UserDataViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Response.User
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : ComponentActivity() {
    private var binding: ActivityProfileBinding? = null
    private val userDataViewModel: UserDataViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setListeners()
        setObserve()
        getUser()

    }

    private fun setObserve() {
        userViewModel.user.observe(this){getUserApi(it)}
    }
    private fun getUserApi(api:ApiResponse<User>){
        when (api) {
            is ApiResponse.Success -> {
                user = api.data
                initProfile(user)
            }
            is ApiResponse.Failure -> {
                Toast.makeText(
                    this, "Ошибка! $api.message", Toast.LENGTH_SHORT
                ).show()
                exitAuth()
            }

            is ApiResponse.Loading -> {
                return
            }
        }
    }

    private fun getUser() {
        userDataViewModel.userData.observe(this) {
            if (it?.accessToken.isNullOrBlank() && it?.phone.isNullOrBlank()) {
                exitAuth()
                return@observe
            }
            onUserSend(it!!.phone)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.imageButtonBack?.setOnClickListener(null)
        binding = null
    }

    private fun setListeners() {
        binding?.imageButtonBack?.setOnClickListener { onBackPress() }
    }

    private fun onUserSend(phone: String) {
        userViewModel.getByPhone(phone,
            object : ICoroutinesErrorHandler {
                override fun onError(message: String) {
                    Toast.makeText(
                        this@ProfileActivity, "Ошибка $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun onBackPress() {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun exitAuth() {
        userDataViewModel.ClearUserData()
        var intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun initProfile(user:User){
        var fio = "${user.surname} ${user.name}"
        binding?.textFIO?.text = fio
        binding?.textFIOEdit?.text = fio
        binding?.textPhone?.text = user.phone
        binding?.textNickname?.text = user.nickname
        binding?.inputMessageAboutMe?.setText(user.aboutMe)
        if(user.photo.isNullOrBlank()){
            return
        }
        Picasso.get()
            .load(user.photo)
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .into(binding?.imageUser)
    }
}