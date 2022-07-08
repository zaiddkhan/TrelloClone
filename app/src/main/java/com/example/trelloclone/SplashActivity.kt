package com.example.trelloclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.trelloclone.firestore.FirestoreClass
import com.example.trelloclone.logins.IntroActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        Handler().postDelayed({

            var currentUserId = FirestoreClass().getCurrentUserId()

            if(currentUserId.isNotEmpty())
                startActivity(Intent(this,MainActivity::class.java))
            else
                startActivity(Intent(this,IntroActivity::class.java))
            finish()
        },2500)
    }
}