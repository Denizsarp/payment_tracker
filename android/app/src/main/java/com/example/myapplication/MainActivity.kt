package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
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


            var currentScreen by remember {mutableStateOf("register")}
            var registerError by remember{mutableStateOf("")}



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
            }
        }
    }
}

