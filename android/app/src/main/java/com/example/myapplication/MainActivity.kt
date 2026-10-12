package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

private val PaymentTrackerColors = lightColorScheme(
    primary = Color(0xFF0B6378),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0F2F7),
    onPrimaryContainer = Color(0xFF003640),
    secondary = Color(0xFF55676B),
    background = Color(0xFFF5F8F9),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE8F0F1),
    error = Color(0xFFB3261E)
)

@Composable
private fun PaymentTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PaymentTrackerColors,
        content = content
    )
}

class MainActivity : ComponentActivity(){
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent{
            PaymentTrackerTheme {

                var username by remember{mutableStateOf("")}
                var email by remember{mutableStateOf("")}
                var password by remember{mutableStateOf("")}
                var targetSpending by remember{mutableStateOf("")}
                var accessToken by remember { mutableStateOf("") }
                var subscriptions by remember {
                    mutableStateOf(listOf<SubscriptionDisplay>())
                }
                var userMemories by remember{mutableStateOf(listOf<SubscriptionDisplay>())}
                var targetSubs by remember {
                    mutableStateOf<SubscriptionDisplay?>(null)
                }

                var totalSpendingMonthly by remember{mutableStateOf("")}

                var subsTitle by remember{mutableStateOf("")}
                var subsAmount by remember{ mutableStateOf("") }
                var payCycle by remember{mutableStateOf("")}


                var currentScreen by remember {mutableStateOf("register")}
                var registerError by remember{mutableStateOf("")}
                var loginError by remember{mutableStateOf("")}
                var creationError by remember{mutableStateOf("")}
                var deletionError by remember{mutableStateOf("")}



                when(currentScreen){

                    "register" -> {
                        ScreenContainer {
                            AppHero(
                                eyebrow = "TAKE CONTROL",
                                title = "Track every payment.",
                                subtitle = "A quieter, clearer way to manage subscriptions."
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            FormCard {
                                Text(
                                    text = "Create your account",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(18.dp))

                                OutlinedTextField(
                                    value = username,
                                    onValueChange = {username = it},
                                    label = {Text("Username")},
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {email = it},
                                    label={Text("E-mail")},
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = {password = it},
                                    label = {Text("Password")},
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = targetSpending,
                                    onValueChange = { newValue ->
                                        if(newValue.all{it.isDigit()}){
                                            targetSpending = newValue
                                        }
                                    },
                                    label = {Text("Target Monthly Spending")},
                                    modifier = Modifier.fillMaxWidth(),
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
                                                currentScreen = "login"
                                            }catch(e : HttpException){
                                                registerError = "Error while registering!"
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 20.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(vertical = 15.dp)
                                ){
                                    Text("Register Now!")
                                }
                                if(registerError.isNotEmpty()){
                                    ErrorCard(registerError)
                                }

                                Button(
                                    onClick = {
                                        currentScreen = "login"
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(vertical = 14.dp)
                                ){
                                    Text("Already registered? Login now!")
                                }
                            }
                        }
                    }







                    "login" ->{
                        ScreenContainer {
                            AppHero(
                                eyebrow = "WELCOME BACK",
                                title = "Your spending, clearly.",
                                subtitle = "Log in to see what renews each month."
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            FormCard {
                                Text(
                                    text = "Login",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(18.dp))

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {email = it},
                                    label = {Text("Username/E-mail")},
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = {password = it},
                                    label = {Text("Password")},
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation()
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
                                                var tempTotal: Int = 0
                                                subscriptions.forEach {sub ->
                                                    tempTotal += sub.amount
                                                }

                                                totalSpendingMonthly = tempTotal.toString()
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
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 20.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(vertical = 15.dp)
                                ){
                                    Text("Login")
                                }
                                if(loginError.isNotEmpty()){
                                    ErrorCard(loginError)
                                }
                            }
                        }
                    }







                    "home" ->{
                        ScreenContainer {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Payment Tracker",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Your recurring payments",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = subscriptions.size.toString(),
                                        modifier = Modifier.padding(
                                            horizontal = 14.dp,
                                            vertical = 9.dp
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(22.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text(
                                        text = "MONTHLY OVERVIEW",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                        style = MaterialTheme.typography.labelLarge,
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Your total monthly Spending:" + totalSpendingMonthly + "TL",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Subscriptions",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            subscriptions.forEach{subs ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(18.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = subs.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = subs.payCycle,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "${subs.amount}" + "TL",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    targetSubs = subs
                                                    currentScreen = "subsDetails"
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(
                                                    horizontal = 14.dp,
                                                    vertical = 7.dp
                                                )
                                            ){
                                                Text("Details")
                                            }
                                        }
                                    }
                                }
                            }

                            if (subscriptions.isEmpty()) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Text(
                                        text = "No subscriptions yet.",
                                        modifier = Modifier.padding(22.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    currentScreen = "subsCreate"
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp),
                                shape = RoundedCornerShape(16.dp)
                            ){
                                Text(
                                    text = "+",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    }






                    "subsCreate" ->{
                        ScreenContainer {
                            AppHero(
                                eyebrow = "NEW SUBSCRIPTION",
                                title = "Add a recurring payment.",
                                subtitle = "Keep the services you pay for visible."
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            FormCard {
                                Text(
                                    text = "NEW SUBSCRIPTION",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(18.dp))

                                OutlinedTextField(
                                    value = subsTitle,
                                    onValueChange = {subsTitle = it},
                                    label = {Text("Subscription Title")},
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = subsAmount,
                                    onValueChange = { input ->
                                        if (input.all { it.isDigit() }) {
                                            subsAmount = input
                                        }
                                    },
                                    label = { Text("Subscription amount") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = payCycle,
                                    onValueChange = {payCycle = it},
                                    label = {Text("Subscription Title")},
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
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

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 20.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(vertical = 15.dp)
                                ){
                                    Text("Create Subscription!")
                                }
                                if(creationError.isNotEmpty()){
                                    ErrorCard(creationError)
                                }
                            }
                        }
                    }

                    // Button mevcut kodunda "subsDetails" diyor, branch ise "subDetails".
                    // İkisini de kabul ederek isimlerini değiştirmeden ekranı açıyoruz.
                    "subDetails", "subsDetails" ->{
                        ScreenContainer {
                            Text(
                                text = "Subscription Details",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Selected recurring payment",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(22.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text(
                                        text = targetSubs?.name ?: "",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    DetailLine(
                                        label = "Amount",
                                        value = targetSubs?.amount?.toString() ?: ""
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    DetailLine(
                                        label = "Payment cycle",
                                        value = targetSubs?.payCycle ?: ""
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    lifecycleScope.launch{
                                        val target = SubscriptionDeleteRequest(
                                            name = targetSubs?.name ?: ""
                                        )
                                        try{
                                            RetrofitClient.api.deleteSubscription(
                                                token = "Bearer $accessToken",
                                                deleteSubs = target
                                            )
                                            currentScreen = "home"
                                        }catch(e : HttpException){
                                            deletionError = when(e.code()){
                                                404 -> "Subscription not found!"
                                                403 -> "Not authenticated!"
                                                else -> "Server Error, please try again later!"
                                            }
                                        }catch(e : Exception){
                                            deletionError = "Server problem occurred!"
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 22.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ),
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(vertical = 15.dp)
                            ){
                                Text("Delete Subscription")
                            }
                            if(deletionError.isNotEmpty()){
                                ErrorCard(deletionError)
                            }
                            Button(
                                onClick = {
                                    targetSubs = subscriptions.first()
                                    deletionError = ""
                                    currentScreen = "home"
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(vertical = 14.dp)
                            ){
                                Text("Home")
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenContainer(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            content = content
        )
    }
}

@Composable
private fun AppHero(
    eyebrow: String,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = eyebrow,
                color = MaterialTheme.colorScheme.primaryContainer,
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f)
            )
        }
    }
}

@Composable
private fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@Composable
private fun ErrorCard(message: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold
        )
    }
}
