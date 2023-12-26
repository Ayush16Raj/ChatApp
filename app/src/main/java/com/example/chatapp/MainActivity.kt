package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.screens.ChatListScreen
import com.example.chatapp.screens.DetailChatScreen
import com.example.chatapp.screens.LoginScreen
import com.example.chatapp.screens.SignupScreen
import com.example.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class DestinationScreen(var route: String){
    object Signup : DestinationScreen("signup")
    object Login : DestinationScreen("login")
    object ChatList : DestinationScreen("chatList")
    object DetailChat : DestinationScreen("detailChat/{chatId}"){
        fun createRoute(id: String) = "detailChat/$id"
    }

}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
    ChatApplicationNavigation()
                }
            }
        }
    }
}

@Composable
fun ChatApplicationNavigation(){
    val navController = rememberNavController()
    var vm = hiltViewModel<ChatViewModel>()

    NavHost(navController = navController, startDestination = DestinationScreen.Signup.route) {
        composable(DestinationScreen.Signup.route) {
            SignupScreen(navController,vm)
        }
        composable(DestinationScreen.Login.route) {
            LoginScreen(navController,vm)
        }
        composable(DestinationScreen.ChatList.route) {
            ChatListScreen(navController,vm)
        }
        composable(DestinationScreen.DetailChat.route) {
            val chatId = it.arguments?.getString("chatId")
            chatId?.let {
                DetailChatScreen(navController,vm,chatId)
            }
        }

    }


}

