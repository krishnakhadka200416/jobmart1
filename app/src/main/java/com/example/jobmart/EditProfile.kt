package com.example.jobmart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.jobmart.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile.*


private const val TAG = "Edit Profile"

private lateinit var firestoreDb : FirebaseFirestore
private lateinit var storageReference : StorageReference
class EditProfile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        auth = FirebaseAuth.getInstance()
        firestoreDb = FirebaseFirestore.getInstance()
        /*
        var goal: String = update_goal.text.toString()
        var education: String = update_education.text.toString()
        var interest: String = update_interest.text.toString()
        var profession: String = update_profession.text.toString()
        */

        firestoreDb.collection("users")
                .document(FirebaseAuth.getInstance().currentUser?.uid as String)
                .get()
                .addOnSuccessListener { document ->
                    supportActionBar?.title = document.data?.getValue("username").toString()
                }
                .addOnFailureListener{exception ->
                    Log.i(TAG, "Failure fetching signed in user", exception)

                }

        if(update_goal.text.toString().isEmpty()) {


            update.setOnClickListener {

                firestoreDb.collection("users")
                        .document(FirebaseAuth.getInstance().currentUser?.uid as String)
                        .update(mapOf (
                                "goal" to update_goal.text.toString(),
                                "education" to update_education.text.toString(),
                                "interest" to update_interest.text.toString(),
                                "profession" to update_profession.text.toString()
                        ))

                // Toast.makeText(this,"Update Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, UserInfo::class.java))
                finish()

            }
        }

        }
    }
