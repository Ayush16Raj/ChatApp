package com.example.chatapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.ChatViewModel
import com.example.chatapp.data.Message

@Composable
fun DetailChatScreen(navController: NavController, vm : ChatViewModel,chatId:String) {
    var reply by rememberSaveable {
        mutableStateOf("")
    }
    val onSendReply = {
        vm.onSendReply(chatId, reply)
        reply = ""
    }
    var chatMessage = vm.chatMessages
    val myUser = vm.userData.value
    val currentChat = vm.chats.value.first{
        it.chatId == chatId
    }
    val chatUser = if(myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1

    LaunchedEffect(key1 = Unit ){
 vm.populateMessages(chatId)
    }

    Column {
ChatHeader(name = chatUser.name?:"") {
    navController.popBackStack()
    vm.depopulateMessage()

}
        MessageBox(modifier = Modifier.weight(1f), chatMessage = chatMessage.value, currentUserId = myUser?.userId?:"")
        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)
    }
}

@Composable
fun MessageBox(modifier: Modifier,chatMessage: List<Message>,currentUserId:String){
    LazyColumn(modifier = modifier){
      items(chatMessage){
          msg->
          val alignment = if(msg.sendBy==currentUserId) Alignment.End else Alignment.Start
          val color = if(msg.sendBy==currentUserId) Color(0xFFBB86FC) else Color(0xFF625b71)
          Column (modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp), horizontalAlignment = alignment){
              Text(text = msg.message?:"",modifier = Modifier
                  .clip(RoundedCornerShape(15.dp))
                  .background(color)
                  .padding(12.dp)
              )
          }
      }
    }
}


@Composable
fun ChatHeader(name: String,onBackClicked:()->Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
    , verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
            .clickable {
                onBackClicked.invoke()
            }
            .padding(8.dp))
        Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp).align(Alignment.CenterVertically))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyBox(reply: String,onReplyChange:(String)->Unit,onSendReply:()->Unit){
    Column(modifier = Modifier.fillMaxWidth()) {

       Row(modifier = Modifier
           .fillMaxWidth()
           .padding(8.dp),
           horizontalArrangement = Arrangement.SpaceBetween) {
           OutlinedTextField(value = reply, onValueChange = onReplyChange, maxLines = 3, shape = CircleShape, label = {
               Text(text = "Type Message")
           }, modifier = Modifier.weight(1f))
           Button(onClick = onSendReply) {
               Text(text = "Send")
           }
       }
    }

}