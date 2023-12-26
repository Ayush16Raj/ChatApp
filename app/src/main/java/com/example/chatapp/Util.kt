package com.example.chatapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

fun navigateTo(navController: NavController,route : String){
    navController.navigate(route){
        popUpTo(route)
        launchSingleTop = true
    }

}

@Composable
fun progressBar(){
    Row(modifier = Modifier
        .alpha(0.5f)
        .background(Color.LightGray)
        .clickable(enabled = false) {}
        .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun checkSignedIn(vm: ChatViewModel,navController: NavController){
    val alreadySignedIn = remember {
        mutableStateOf(false)
    }
    val signIn = vm.signIn.value
    if (signIn && !alreadySignedIn.value){
        alreadySignedIn.value = true
        navController.navigate(DestinationScreen.ChatList.route){
            popUpTo(0)
        }
    }
}

@Composable
fun titleText(txt:String){
    Text(
        text = txt,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        modifier = Modifier.padding(15.dp)
    )
}

@Composable
fun commonRow(name:String?,onItemClick:()->Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .clickable { onItemClick.invoke() },
        verticalAlignment = Alignment.CenterVertically){
       Text(text = name ?: "--",
           fontWeight = FontWeight.Bold,
           modifier = Modifier.padding(start = 30.dp), fontSize = 20.sp
       )
    }

}