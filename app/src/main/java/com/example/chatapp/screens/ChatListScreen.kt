package com.example.chatapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.ChatViewModel
import com.example.chatapp.DestinationScreen
import com.example.chatapp.commonRow
import com.example.chatapp.navigateTo
import com.example.chatapp.progressBar
import com.example.chatapp.titleText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(navController: NavController, vm : ChatViewModel) {

    val inprogress = vm.inProgressChat
    if(inprogress.value){
        progressBar()
    }else{
        val chats = vm.chats.value
        val userData = vm.userData.value
        val showDialog = remember {
            mutableStateOf(false)
        }
        val onFabClick:()->Unit={
            showDialog.value = true
        }
        val onDismiss:()->Unit={
            showDialog.value = false
        }
        val onAddChat:(String)->Unit={
            vm.onAddChat(it)
            showDialog.value = false
        }

        Scaffold(floatingActionButton = {
            FAB(showDialog = showDialog.value,
                onFabClick = onFabClick ,
                onDismiss =  onDismiss ,
                onAddChat = onAddChat)
        },
            content = {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(it)) {
 titleText(txt = "Chats")
                    
                    if(chats.isEmpty()){
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        , verticalArrangement = Arrangement.Center) {
                            Text(text = "Add chats to start")
                        }
                    }else{
                        LazyColumn(modifier = Modifier.weight(1f)){
                            items(chats) { chat ->
                                val chatUser = if (chat.user1.userId == userData?.userId) {
                                    chat.user2
                                } else {

                                    chat.user1
                                }
                                Divider(thickness = Dp.Hairline)
                                commonRow(name = chatUser.name) {
                                    chat.chatId?.let {
                                        navigateTo(
                                            navController,
                                            DestinationScreen.DetailChat.createRoute(id = it)
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            })

    }


    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        , horizontalArrangement = Arrangement.End) {
        Text(text = "Logout", modifier = Modifier.clickable {
           vm.logout()
            navigateTo(navController = navController,DestinationScreen.Login.route)
        })
    }



}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAB(
    showDialog: Boolean,
    onFabClick:() -> Unit,
    onDismiss:() -> Unit,
    onAddChat: (String) -> Unit
){

        val addChatNumber = remember{
            mutableStateOf("")
        }
        if (showDialog){
            AlertDialog(onDismissRequest = { onDismiss.invoke()
                                           addChatNumber.value=""},
                confirmButton = {
                    Button(onClick = { onAddChat(addChatNumber.value) }) {
                        Text(text = "Add Chat")
                    }
                },
                title = { Text(text = "Add Friends email")},
                text = {
                    OutlinedTextField(value = addChatNumber.value,
                        onValueChange = {
                            addChatNumber.value = it

                        }
                    )
                })

        }else{
            FloatingActionButton(
                onClick =  onFabClick ,
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }





}