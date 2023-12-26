package com.example.chatapp.data

data class UserData(
    var userId:String?="",
    var name:String?="",
    var email:String?=""

){
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "email" to email,

    )
}

data class ChatData(
    val chatId: String?="",
    val user1: ChatUser=ChatUser(),
    val user2: ChatUser=ChatUser()
)

data class Message(
    var sendBy: String?="",
    val message: String?="",
    val timestamp: String?=""
)

data class ChatUser(
    val userId: String?="",
    val name: String?="",
    val email: String?=""
)
