package com.example.jobmart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.jobmart.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


private const val TAG = "Edit Profile"
class EditProfile : AppCompatActivity() {

    private lateinit var  firestoreDb: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        firestoreDb = FirebaseFirestore.getInstance()
        var uname: String

        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { document ->
                uname = document.data?.getValue("username").toString()
                supportActionBar?.title = uname
            }
            .addOnFailureListener{exception ->
                Log.i(TAG, "Failure fetching signed in user", exception)

            }


    }
}