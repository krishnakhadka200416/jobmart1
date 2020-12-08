package com.example.jobmart

import android.app.Activity
import android.content.Context
import android.util.Log

import com.example.jobmart.util.DateUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.jobmart.models.ChatMessage
import com.example.jobmart.models.ChatUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow (val chatMessage: ChatMessage, val context: Context) : Item<ViewHolder>() {
    private lateinit var firestoreDb : FirebaseFirestore
    var chatPartnerUser: ChatUser? = null
    var uname: String =""
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.latest_message_textview.text = chatMessage.text

        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }
      //  Log.i("LAtestmsg row", chatPartnerId)
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(chatPartnerId.toString())
            .get()
            .addOnSuccessListener { document->
                uname = document.data?.getValue("username").toString()
                Log.i("LAtestmsg row", uname)
               }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(ChatUser::class.java)
                viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.username.toString()
                viewHolder.itemView.latest_msg_time.text = DateUtil.getFormattedTime(chatMessage.timestamp)


            }


        })


    }

}