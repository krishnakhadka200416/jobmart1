package com.example.jobmart

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.jobmart.models.ChatMessage
import com.example.jobmart.models.ChatUser
import com.example.jobmart.util.DateUtil.getFormattedTimeChatLog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.math.BigInteger
import java.security.MessageDigest
var user_to: ChatUser = ChatUser("","")
class ChatLogActivity : AppCompatActivity() {

    private lateinit var firestoreDb : FirebaseFirestore
    val adapter = GroupAdapter<ViewHolder>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        val to_user = getIntent()
        var user_toid = intent.getStringExtra("usertoid")
        var uname: String = ""
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(intent.getStringExtra("usertoid").toString())
            .get()
            .addOnSuccessListener { document->
                uname = document.data?.getValue("username").toString()
                supportActionBar?.title = uname
            }
            .addOnFailureListener{exception ->
                Log.i("ViewUSerActivity", "Failure fetching user", exception)

            }

        user_to = ChatUser(intent.getStringExtra("usertoid").toString(), uname)
        swiperefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.teal_700))
        supportActionBar?.title = user_to.username

        recyclerview_chat_log.adapter = adapter
        listenForMessages()

        send_button_chat_log.setOnClickListener {
            performSendMessage()

        }

    }
    private fun listenForMessages() {
        swiperefresh.isEnabled = true
        swiperefresh.isRefreshing = true

        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = user_to.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("ChatLogActivity", "database error: " + databaseError.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("ChatLogActivity", "has children: " + dataSnapshot.hasChildren())
                if (!dataSnapshot.hasChildren()) {
                    swiperefresh.isRefreshing = false
                    swiperefresh.isEnabled = false
                }
            }
        })

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(ChatMessage::class.java)?.let {


                    if (it.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = Message.currentUser ?: return

                        adapter.add(ChatFromItem(it.text, currentUser, it.timestamp))


                    } else {
                        adapter.add(ChatToItem(it.text, user_to, it.timestamp))
                    }

                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                swiperefresh.isRefreshing = false
                swiperefresh.isEnabled = false
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            }

        })

    }
    private fun performSendMessage() {
        val text = edittext_chat_log.text.toString()
        if (text.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = user_to.uid


        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("ChatLoagActivity", "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.smoothScrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)


        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }

}
class ChatFromItem(val text: String, val user: ChatUser, val timestamp: Long) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textview_from_row.text = text
        viewHolder.itemView.from_msg_time.text = getFormattedTimeChatLog(timestamp)
        fun getProfileImageURl(username:String): String{
            val digest = MessageDigest.getInstance("MD5");
            val hash = digest.digest(username.toByteArray());
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return "https://www.gravatar.com/avatar/$hex?d=identicon";
        }
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        Glide.with(targetImageView.context).load(getProfileImageURl(user.username)).into(targetImageView)

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}
class ChatToItem(val text: String, val user: ChatUser, val timestamp: Long) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        viewHolder.itemView.to_msg_time.text = getFormattedTimeChatLog(timestamp)
        val db = FirebaseFirestore.getInstance()
        fun getProfileImageURl(username:String): String{
            val digest = MessageDigest.getInstance("MD5");
            val hash = digest.digest(username.toByteArray());
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return "https://www.gravatar.com/avatar/$hex?d=identicon";
        }
        var uname:String =""
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        db.collection("users")
            .document(user_to.uid)
            .get()
            .addOnSuccessListener { document->
                uname = document.data?.getValue("username").toString()

                Glide.with(targetImageView.context).load(getProfileImageURl(uname)).into(targetImageView)


            }
            .addOnFailureListener{exception ->
                Log.i("hi", "Failure fetching signed in user", exception)

            }





    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}