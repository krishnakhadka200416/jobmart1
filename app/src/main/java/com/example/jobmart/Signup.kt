package com.example.jobmart

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobmart.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_user_info.*


private const val TAG = "CreateJob"
private  const val PICK_PHOTO_CODE = 1234
private lateinit var firestoreDb : FirebaseFirestore
private lateinit var storageReference : StorageReference

class Signup : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    auth = FirebaseAuth.getInstance()

    register.setOnClickListener{
        signupuser()

    }
}
private fun signupuser(){
    val user1 = User(
            first_name.text.toString(),
            middle_name.text.toString(),
            last_name.text.toString()


    )
    if(email.text.toString().isEmpty()){
        email.error = "Please enter email"
        email.requestFocus()
        return
    }
    if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
        email.error = "Please enter email"
        email.requestFocus()
        return
    }
    if(first_name.text.toString().isEmpty()){
        first_name.error = "Please enter first name"
        first_name.requestFocus()
        return
    }
    if(last_name.text.toString().isEmpty()){
        last_name.error = "Please enter first name"
        last_name.requestFocus()
        return
    }
    if(password.text.toString().isEmpty()){
        password.error ="please enter password"
        password.requestFocus()
        return
    }
    if(password.text.toString().length <6)
    {
        password.error ="please enter password at least 6 character"
        password.requestFocus()
        return

    }
    auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val testUser = FirebaseAuth.getInstance()
                    .currentUser //getting the current logged in users id
                val userUid = testUser!!.uid
                val uidInput = userUid
                val db = FirebaseFirestore.getInstance()
                val user: MutableMap<String,Any> = HashMap()
                    user["education"] = education1.text.toString()
                    user["goal"] = goal1.text.toString()
                    user["interest"] = interest1.text.toString()
                    user["profession"]=  profession1.text.toString()
                    user["username"] = first_name.text.toString()

                db.collection("users").document(uidInput)
                    .set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Home::class.java))
                        finish()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Sign Up notsuccessful", Toast.LENGTH_SHORT).show()
                    }




            } else {
                Toast.makeText(baseContext, "Email Already Used or invalid email.",
                    Toast.LENGTH_SHORT).show()
            }

        }
}
}