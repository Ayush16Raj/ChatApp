package com.example.chatapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatapp.data.CHATS
import com.example.chatapp.data.ChatData
import com.example.chatapp.data.ChatUser
import com.example.chatapp.data.Event
import com.example.chatapp.data.MESSAGE
import com.example.chatapp.data.Message
import com.example.chatapp.data.USER_NODE
import com.example.chatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val auth : FirebaseAuth,
    var db : FirebaseFirestore
)  : ViewModel()
{

    var inProgress = mutableStateOf(false)
    var inProgressChat = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressChatMsg =  mutableStateOf(false)
    var currentChatMsgListener: ListenerRegistration?=null



    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser !=null
        currentUser?.uid.let {
            if (it != null) {
                getUserData(it)
            }
        }
    }
    fun populateMessages(chatId: String){
        inProgressChatMsg.value = true
        currentChatMsgListener = db.collection(CHATS).document(chatId).collection(MESSAGE)
            .addSnapshotListener { value, error ->
                if(error!=null){
                    handleException(error)

                }
                if(value!=null){
                    chatMessages.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.sortedBy {
                        it.timestamp
                    }
                    inProgressChatMsg.value = false
                }
            }
    }
    fun depopulateMessage(){
        chatMessages.value = listOf()
        currentChatMsgListener = null
    }



    fun populateChats(){
        inProgressChat.value= true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId",userData.value?.userId),
                Filter.equalTo("user2.userId",userData.value?.userId),
            )
        ).addSnapshotListener{
            value,error->
            if (error!=null){
                handleException(error)

            }
            if(value!=null){
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProgressChat.value = false
            }
        }
    }

    fun onSendReply(chatId: String,message: String){
        val time = Calendar.getInstance().time.toString()
        val msg = Message(userData.value?.userId,message,time)
        db.collection(CHATS).document(chatId).collection(MESSAGE).document().set(msg)
    }


    fun signUp(name: String,email: String,password: String){
        inProgress.value = true
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()){
            handleException(customMessage = "Please Fill all fields")
            return
        }
        inProgress.value = true
        db.collection(USER_NODE).whereEqualTo("email",email).get().addOnSuccessListener {
            if (it.isEmpty){
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    if(it.isSuccessful){
                        signIn.value = true
                        createOrUpdateProfile(name,email)

                    }else{
                        handleException(it.exception, customMessage = "SignUp Failed")
                        inProgress.value = false

                    }
                }
            }else{
                handleException(customMessage = "Email id already exists")
                inProgress.value = false
            }
        }

    }

     fun createOrUpdateProfile(name: String,email: String) {
        var uid = auth.currentUser?.uid
        val userData = UserData(
          userId = uid,
            name = name,
            email = email
        )
        uid?.let {
            inProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if(it.exists()){
// update user data
                }else{
                    db.collection(USER_NODE).document(uid).set(userData)
                    getUserData(uid)
                }
                inProgress.value = false

            }.addOnFailureListener {
                handleException(it,"Cannot Retrieve User")
            }


        }

    }

     fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if(error!=null){
                handleException(error,"Can not retrieve user")
            }
            if(value!=null){
                var user = value.toObject<UserData>()
                userData.value = user
                inProgress.value = false
                populateChats()
            }

        }
    }

    fun handleException(exception: Exception?=null,customMessage: String=""){
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage?:""
        val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage

        eventMutableState.value = Event(message)
        inProgress.value = false


    }

    fun login(email: String,password: String){

        if (email.isEmpty() || password.isEmpty()){
            handleException(customMessage = "Please Fill all fields")
            return
        }else{
            inProgress.value = true
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        signIn.value = true
                        inProgress.value = false
                        auth.currentUser?.uid?.let {
                            getUserData(it)
                        }
                    }else{
                        handleException(it.exception,"Login Failed")
                    }
                }
        }
    }
    fun logout(){
        auth.signOut()
        signIn.value = false
        userData.value = null
        eventMutableState.value = Event("Logged Out")
        depopulateMessage()
        currentChatMsgListener = null

    }

    fun onAddChat(email: String) {
        var uid = auth.currentUser?.uid
   if(email.isEmpty()){
       handleException(customMessage = "Field should not be empty")
   }else{

           db.collection(CHATS).where(Filter.or(
               Filter.and(
                   Filter.equalTo("user1.email",email),
                   Filter.equalTo("user2.email", userData.value?.email)

               ),
               Filter.and(
                   Filter.equalTo("user1.email",userData.value?.email),
                   Filter.equalTo("user2.email", email)

               )
           )).get().addOnSuccessListener {
               if (it.isEmpty){
                   db.collection(USER_NODE).whereEqualTo("email",email).get().addOnSuccessListener {
                       if(it.isEmpty){
                           handleException(customMessage = "Email not found")
                       }else{
                           val chatPartner = it.toObjects<UserData>()[0]
                           val id =  db.collection(CHATS).document().id
                           val chat = ChatData(
                               chatId = id,
                               ChatUser( userData.value?.userId,
                                   userData.value?.name,
                                   userData.value?.email),
                               ChatUser(chatPartner.userId,
                                   chatPartner.name,
                                   chatPartner.email)
                           )
                          db.collection(CHATS).document(id).set(chat)

                       }
                   }.addOnFailureListener {
                       handleException(it)
                   }
               }else{
                   handleException(customMessage = "Chat already exist")
               }
           }
       }
   }
    }



