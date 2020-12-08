package com.example.jobmart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.jobmart.models.Post
import com.example.jobmart.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_info.*
import java.math.BigInteger
import java.security.MessageDigest

class ViewUserActivity : AppCompatActivity() {

    private lateinit var firestoreDb : FirebaseFirestore
    private lateinit var usersprofile :MutableList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_user)
        fun getProfileImageURl(username:String): String{
            val digest = MessageDigest.getInstance("MD5");
            val hash = digest.digest(username.toByteArray());
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return "https://www.gravatar.com/avatar/$hex?d=identicon";
        }
        val intent = getIntent()
        val db = FirebaseFirestore.getInstance()
        var uname: String = ""
        var edu:String
        var pro: String
        var inte: String
        var goa: String
        db.collection("users")
                .document(intent.getStringExtra("usertoid").toString())
                .get()
                .addOnSuccessListener { document->
                    uname = document.data?.getValue("username").toString()
                    edu = document.data?.getValue("education").toString()
                    pro = document.data?.getValue("profession").toString()
                    inte = document.data?.getValue("interest").toString()
                    goa = document.data?.getValue("goal").toString()
                    u_name.setText(uname)
                    education.setText(edu)
                    profession.setText(pro)
                    interest.setText(inte)
                    goal.setText(goa)
                    supportActionBar?.title = uname
                    Glide.with(applicationContext).load(getProfileImageURl(uname.toString())).into(image_view)

                }
                .addOnFailureListener{exception ->
                    Log.i("ViewUSerActivity", "Failure fetching signed in user", exception)

                }



        }
    }
