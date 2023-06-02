package com.example.trelloclone.data

import com.example.trelloclone.boards.Board


data class User(
    val userName : String = "",
    val imageUrl : String = "",
    val email : String = "",
    val boards : List<Board> = emptyList()
)

