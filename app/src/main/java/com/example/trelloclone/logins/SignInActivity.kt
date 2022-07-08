package com.example.trelloclone.logins

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.trelloclone.MainActivity
import com.example.trelloclone.R
import com.example.trelloclone.databinding.ActivitySignInBinding
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.BaseActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding:ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setUpActionBar()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarSignInActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
    }

    private fun signInRegisteredUser(){
        val email : String = binding.etEmail.text.toString().trim{it <= ' '}
        val password : String = binding.etPassword.text.toString().trim{it <= ' '}

        if(validateForm(email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if(task.isSuccessful){
                        val user = auth.currentUser
                        startActivity(Intent(this,MainActivity::class.java))
                    }else{
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }

                }
        }
    }

    fun signInSuccess(user:User){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun validateForm(email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }

            else -> {
                true
            }
        }
    }
}