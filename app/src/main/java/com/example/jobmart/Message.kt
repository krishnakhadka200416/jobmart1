package com.example.jobmart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.jobmart.models.ChatMessage
import com.example.jobmart.models.ChatUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_message.*

class Message : AppCompatActivity() {

    private val adapter = GroupAdapter<ViewHolder>()
    private val MessagesMap = HashMap<String, ChatMessage>()

    companion object {
        var currentUser: ChatUser? = null
        val TAG = Message::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        supportActionBar?.title = "Messages"
        val intent = getIntent()
        var usertoid: String? = intent.getStringExtra("usertoid")

        recyclerview_latest_messages.adapter = adapter
        swiperefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.teal_700))

        getcurrentuser()
        LatestMessage()

        adapter.setOnItemClickListener { item, _ ->
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            Log.i("Message",row.chatPartnerUser?.uid.toString())
            intent.putExtra("usertoid",row.chatPartnerUser?.uid.toString())
            startActivity(intent)
        }


    }

    private fun getcurrentuser() {
        val uid = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                currentUser = dataSnapshot.getValue(ChatUser::class.java)
            }

        })
    }
    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        MessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it, this))
        }
        swiperefresh.isRefreshing = false
    }
    private fun LatestMessage() {
        swiperefresh.isRefreshing = true
        val fromId = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "database error: " + databaseError.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "has children: " + dataSnapshot.hasChildren())
                if (!dataSnapshot.hasChildren()) {
                    swiperefresh.isRefreshing = false
                }
            }

        })
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(ChatMessage::class.java)?.let {
                    MessagesMap[dataSnapshot.key!!] = it
                    refreshRecyclerViewMessages()
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(ChatMessage::class.java)?.let {
                    MessagesMap[dataSnapshot.key!!] = it
                    refreshRecyclerViewMessages()
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })

    }
}