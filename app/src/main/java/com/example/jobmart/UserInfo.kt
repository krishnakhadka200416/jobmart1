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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_sign_in.view.*
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.item_post.view.*
import java.math.BigInteger
import java.security.MessageDigest

private const val TAG = "UserInfo"

private lateinit var storageReference : StorageReference

class UserInfo : AppCompatActivity() {

    private var signedInUser: User? = null
    private lateinit var firestoreDb : FirebaseFirestore
    private lateinit var usersprofile :MutableList<Post>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        fun getProfileImageURl(username:String): String{
            val digest = MessageDigest.getInstance("MD5");
            val hash = digest.digest(username.toByteArray());
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return "https://www.gravatar.com/avatar/$hex?d=identicon";
        }
        val testUser = FirebaseAuth.getInstance()
            .currentUser //getting the current logged in users id
        val userUid = testUser!!.uid
        val uidInput = userUid
        val db = FirebaseFirestore.getInstance()
        var uname: String =""
        var edu:String
        var pro: String
        var inte: String
        var goa: String
        db.collection("users")
                .document(uidInput)
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
                    Glide.with(applicationContext).load(getProfileImageURl(uname.toString())).into(image_view)
                    supportActionBar?.title = uname
                }
                .addOnFailureListener{exception ->
                    Log.i(TAG, "Failure fetching signed in user", exception)

                }




        edit_profile.setOnClickListener {
            val intent = Intent (this, EditProfile::class.java)
            startActivity(intent)
            finish()
        }





    }
}