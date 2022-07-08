package com.example.trelloclone.utils

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.trelloclone.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExit = false

    private lateinit var mProgressDialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text:String){
        mProgressDialog = Dialog(this)

        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit(){
        if(doubleBackToExit){
            super.onBackPressed()
            return
        }
        this.doubleBackToExit = true
        Toast.makeText(this, "Click back again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({doubleBackToExit = false },2000)
    }

    fun showErrorSnackBar(message:String){
        val snackbar = Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snackbar_error_color))
        snackbar.show()
    }
}