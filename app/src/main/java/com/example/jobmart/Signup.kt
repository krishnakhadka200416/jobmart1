package com.example.jobmart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signup.*

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
                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Home::class.java))
                finish()
            } else {
                Toast.makeText(baseContext, "Email Already Used or invalid email.",
                    Toast.LENGTH_SHORT).show()
            }

        }
}
}