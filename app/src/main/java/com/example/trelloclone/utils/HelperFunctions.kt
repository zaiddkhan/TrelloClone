package com.example.trelloclone.utils

import com.example.trelloclone.data.User
import com.example.trelloclone.ui.googlelogin.GoogleUserData


fun GoogleUserData.toTrelloUser() : User {
    return User(username!!,imageUrl!!,userId)
}