package com.example.universityairlines.login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.example.universityairlines.homepage.HomepageActivity
import com.example.universityairlines.homepage.HomepageActivity.Companion.EXTRAKEY
import com.example.universityairlines.registration.RegistrationActivity
import com.example.universityairlines.repository.UserRepository
import com.example.universityairlines.databinding.LoginLayoutBinding
import com.example.universityairlines.model.ApiResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    lateinit var bottoneRegistrazione: Button

    private lateinit var binding: LoginLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val progressDialog = ProgressDialog(this@LoginActivity)

        binding = LoginLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginbutton.setOnClickListener { checkUser() }

        binding.loginbutton.setOnClickListener {
            checkUser()
            it.isEnabled = false
        }


        //Gestione comportamento bottone registrazione
        bottoneRegistrazione = binding.registerbutton
        bottoneRegistrazione.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

    }

    fun checkUser() {
        lifecycleScope.launch {
            val mail = binding.edittextemail.text.toString()
            val pwd = binding.edittextpassword.text.toString()
            val progressDialog = ProgressDialog(this@LoginActivity)
            progressDialog.setTitle("Loading")
            progressDialog.show()

            when (val result = UserRepository.getUser(mail, pwd)) {
                is ApiResult.Success -> {
                    val intent = Intent(this@LoginActivity, HomepageActivity::class.java)
                    intent.putExtra(EXTRAKEY, result.value.firstName)
                    progressDialog.hide()
                    startActivity(intent)
                    finish()

                }
                is ApiResult.Failure -> {
                    MaterialAlertDialogBuilder(this@LoginActivity)
                        .setTitle("404 NOT FOUND")
                        .setPositiveButton(
                            "Ok"
                        ) { dialog, which -> dialog?.dismiss() }
                        .setMessage(
                            result.errorResponse?.message ?: "User Not Found"
                        ).show()
                    binding.loginbutton.isEnabled = true
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.loginbutton.isEnabled = true
    }
}