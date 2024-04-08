package KlepetChat.Activities

import KlepetChat.Activities.Data.Constants
import KlepetChat.WebApi.Implementations.ApiResponse
import KlepetChat.WebApi.Implementations.ViewModels.ChatViewModel
import KlepetChat.WebApi.Implementations.ViewModels.TokenViewModel
import KlepetChat.WebApi.Implementations.ViewModels.UserViewModel
import KlepetChat.WebApi.Models.Exceptions.ICoroutinesErrorHandler
import KlepetChat.WebApi.Models.Request.UserRegister
import KlepetChat.WebApi.Models.Response.User
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.klepetchat.R
import com.example.klepetchat.databinding.ActivityRegisterBinding
import com.example.klepetchat.databinding.AlertDialogValidateCodePhoneBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import kotlin.random.Random

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


    @SuppressLint("MissingPermission")
    private fun onValidatePhone() {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var phone = telephonyManager.line1Number
        if (phone.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Не удалось получить номер телефона! Функция сброса пароля заблокирована",
                Toast.LENGTH_SHORT
            ).show()
            postCreate()
            return
        }
        var smsCode = Random.nextInt(10000, 100000)
        Log.d("u","$smsCode")
        var dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        var view =
            LayoutInflater.from(dialog.context)
                .inflate(R.layout.alert_dialog_validate_code_phone, null)
        var bindingSMS = AlertDialogValidateCodePhoneBinding.bind(view)

        dialog.setView(view)
        dialog.setNegativeButton("Отменить") { dialog, _ ->
            dialog?.dismiss()
        }

        val alertDialog = dialog.create()

        bindingSMS.butConfirm.setOnClickListener {
            val enteredCode = bindingSMS.editTextCode.text.toString()
            if (enteredCode == smsCode.toString()) {
                postCreate()
            } else {
                Toast.makeText(this, "Неверный код", Toast.LENGTH_SHORT).show()
            }
            alertDialog.dismiss()
        }
        alertDialog.show()
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

        val permission = Manifest.permission.READ_PHONE_STATE
        if (ActivityCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            return
        }

        onValidatePhone()

    }

    private fun postCreate(){
        userViewModel.postCreate(
            UserRegister(
                binding?.surnameField?.text.toString(),
                binding?.nameField?.text.toString(),
                binding?.phoneField?.text.toString(),
                binding!!.passwordField.text.toString()
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

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.READ_PHONE_STATE
                ), Constants.PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.PERMISSION_REQUEST_CODE -> {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_NUMBERS
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "Для регистрации, нужны разрешения!!!",
                        Toast.LENGTH_SHORT).show()
                    return
                }
                onValidatePhone()
            }

            else -> throw IllegalStateException("Unexpected value: $requestCode")
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
