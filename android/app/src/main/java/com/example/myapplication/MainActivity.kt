package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.text.KeyboardOptions




class MainActivity : ComponentActivity(){
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent{
            var username by remember{mutableStateOf("")}
            var email by remember{mutableStateOf("")}
            var password by remember{mutableStateOf("")}
            var targetSpending by remember{mutableStateOf("")}
            var accessToken by remember { mutableStateOf("") }
            var subscriptions by remember {
                mutableStateOf(listOf<SubscriptionDisplay>())
            }
            var userMemories by remember{mutableStateOf(listOf<SubscriptionDisplay>())}

            var subsTitle by remember{mutableStateOf("")}
            var subsAmount by remember{ mutableStateOf("") }
            var payCycle by remember{mutableStateOf("")}


            var currentScreen by remember {mutableStateOf("register")}
            var registerError by remember{mutableStateOf("")}
            var loginError by remember{mutableStateOf("")}
            var creationError by remember{mutableStateOf("")}



            when(currentScreen){

                "register" -> {
                    Column{
                        Text("     ")
                        Text("     ")
                        Text("     ")
                        Text("     ")
                        OutlinedTextField(
                            value = username,
                            onValueChange = {username = it},
                            label = {Text("Username")}
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = {email = it},
                            label={Text("E-mail")}
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = {password = it},
                            label = {Text("Password")}
                        )
                        OutlinedTextField(
                            value = targetSpending,
                            onValueChange = { newValue ->
                                if(newValue.all{it.isDigit()}){
                                    targetSpending = newValue
                                }
                            },
                            label = {Text("Target Monthly Spending")},
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine=true
                        )

                        Button(
                            onClick = {
                                lifecycleScope.launch{
                                    val registerReq = RegisterRequest(
                                        username = username,
                                        email = email,
                                        password = password,
                                        targetSpending = targetSpending.toInt()
                                    )
                                    try{
                                        RetrofitClient.api.register(registerReq)
                                    }catch(e : HttpException){
                                        registerError = "Error while registering!"
                                    }
                                }
                            }
                        ){
                            Text("Register Now!")
                        }
                        if(registerError.isNotEmpty()){
                            Text(registerError)
                        }
                        else{
                            currentScreen = "login"
                        }

                        Button(
                            onClick = {
                                currentScreen = "login"
                            }
                        ){
                            Text("Already registered? Login now!")
                        }

                    }

                }









                "login" ->{
                    Column{
                        Text("Login")
                        Text("      ")
                        Text("      ")
                        Text("      ")
                        Text("      ")
                        Text("      ")
                        OutlinedTextField(
                            value = email,
                            onValueChange = {email = it},
                            label = {Text("Username/E-mail")}
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = {password = it},
                            label = {Text("Password")}
                        )

                        Button(
                            onClick = {
                                lifecycleScope.launch{
                                    try{
                                        val response = RetrofitClient.api.login(
                                            email = email,
                                            password = password
                                        )
                                        accessToken = response.accessToken
                                        subscriptions = RetrofitClient.api.getSubscriptions(
                                            token = "Bearer $accessToken"
                                        )
                                        currentScreen = "home"

                                    }catch(e: HttpException){
                                        loginError = when(e.code()){
                                            401 -> "User not found!"
                                            403 -> "Incorrect Credentials"
                                            else -> "${e.code()}"
                                        }
                                    }catch(e : Exception){
                                        loginError = "Server Error! or 500!"
                                    }
                                }
                            }
                        ){
                            Text("Login")
                        }
                        if(loginError.isNotEmpty()){
                            Text(loginError)
                        }
                    }
                }









                "home" ->{
                    Column{
                        Text("     ")
                        Text("     ")
                        Text("     ")
                        Text("     ")
                        Text("     ")

                        subscriptions.forEach{subs ->
                            Text(subs.name)
                            Text("${subs.amount}" + "TL")
                            Text(subs.payCycle)
                        }
                        Text("     ")
                        Text("     ")
                        Button(
                            onClick = {
                                currentScreen = "subsCreate"
                            }
                        ){
                            Text("+")
                        }

                    }
                }






                "subsCreate" ->{
                    Column{
                        Text("     ")
                        Text("     ")
                        Text("     ")
                        Text("NEW SUBSCRIPTION")

                        OutlinedTextField(
                            value = subsTitle,
                            onValueChange = {subsTitle = it},
                            label = {Text("Subscription Title")}
                        )
                        OutlinedTextField(
                            value = subsAmount,
                            onValueChange = { input ->
                                if (input.all { it.isDigit() }) {
                                    subsAmount = input
                                }
                            },
                            label = { Text("Subscription amount") }
                        )
                        OutlinedTextField(
                            value = payCycle,
                            onValueChange = {payCycle = it},
                            label = {Text("Subscription Title")}
                        )
                        Button(
                            onClick = {
                                val newSubscription = SubscriptionCreateRequest(
                                    name = subsTitle,
                                    amount = subsAmount.toIntOrNull() ?: 0,
                                    payCycle = payCycle

                                )
                                lifecycleScope.launch{
                                    try{
                                        val createResponse = RetrofitClient.api.createSubscription(
                                            token = "Bearer $accessToken",
                                            createSubs = newSubscription
                                        )
                                        subscriptions  = subscriptions + createResponse
                                    }catch(e : HttpException){
                                        creationError = when(e.code()){
                                            400 -> "Bad request error"
                                            else -> "Unknown creation error!"
                                        }
                                    }catch(e : Exception){
                                        creationError = "Server problem!"
                                    }
                                }

                            }
                        ){
                            Text("Create Subscription!")
                        }
                        if(creationError.isNotEmpty()){
                            Text(creationError)
                        }
                    }
                }
            }
        }
    }
}

