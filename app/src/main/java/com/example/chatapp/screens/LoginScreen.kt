package com.example.chatapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chatapp.ChatViewModel
import com.example.chatapp.DestinationScreen
import com.example.chatapp.checkSignedIn
import com.example.chatapp.navigateTo
import com.example.chatapp.progressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController,vm : ChatViewModel) {


    checkSignedIn(vm = vm, navController =navController)

    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight()
            .verticalScroll(
                rememberScrollState()
            ), horizontalAlignment = Alignment.CenterHorizontally) {

            val emailState = remember{
                mutableStateOf(TextFieldValue())
            }
            val passwordState = remember{
                mutableStateOf(TextFieldValue())
            }
            Text(text = "Login",
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )


            OutlinedTextField(value = emailState.value, onValueChange = {
                emailState.value = it
            },
                label = { Text(text = "Email") },
                modifier = Modifier.padding(5.dp), maxLines = 1)
            OutlinedTextField(value = passwordState.value, onValueChange = {
                passwordState.value = it
            },
                label = { Text(text = "Password") },
                modifier = Modifier.padding(5.dp), maxLines = 1)
            Button(onClick = {
                vm.login(
                    emailState.value.text,
                    passwordState.value.text
                )
            },
                modifier = Modifier.padding(5.dp)) {
                Text(text = "Sign In")

            }
            Text(text = "New User ? Go to SignUp - >",
                modifier = Modifier
                    .padding(5.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.Signup.route)
                    })
        }

    }
    if(vm.inProgress.value){
        progressBar()
    }

}