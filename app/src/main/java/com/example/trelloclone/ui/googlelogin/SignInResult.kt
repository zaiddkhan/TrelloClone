package com.example.trelloclone.ui.googlelogin

data class SignInResult(
    val data : GoogleUserData?,
    val errorMessage : String?
)

data class GoogleUserData(
    val userId : String,
    val username : String?,
    val imageUrl : String?,
    val email : String?
)