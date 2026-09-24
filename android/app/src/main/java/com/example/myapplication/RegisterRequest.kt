package com.example.myapplication

data class RegisterRequest(
    val username:String,
    val email:String,
    val password:String,
    val targetSpending: Int
)