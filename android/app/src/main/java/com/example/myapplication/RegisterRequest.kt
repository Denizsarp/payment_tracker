package com.example.myapplication

data class RegisterRequest(
    val username:String,
    val email:String,
    val password:String,
    val targetSpending: Int
)

data class LoginResponse(
    val accessToken : String,
    val tokenType :String
)

data class SubscriptionDisplay(
    val name:String,
    val amount: Int,
    val payCycle:String
)

data class SubscriptionCreateRequest(
    val name: String,
    val amount: Int,
    val payCycle: String
)