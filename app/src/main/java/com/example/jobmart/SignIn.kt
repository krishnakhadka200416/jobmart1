package com.example.jobmart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.password
import kotlinx.android.synthetic.main.activity_signup.*

class SignIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var btn_click_me2: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

    btn_click_me2 = findViewById(R.id.login) as Button
    btn_click_me2.setOnClickListener {
        btn_click_me2.isEnabled = false
        signinuser()

    }
    val btn_click_me = findViewById(R.id.signup) as Button
    btn_click_me.setOnClickListener {
        startActivity(Intent(this, Signup::class.java))
        finish()
    }
    forgotpassword.setOnClickListener {


            forgotpassword()


    }



}

public override fun onStart() {
    super.onStart()
    // Check if user is signed in (non-null) and update UI accordingly.
    val currentUser: FirebaseUser? = auth.currentUser
    updateUI(currentUser)
}

fun updateUI(currentUser: FirebaseUser?) {


}
private fun forgotpassword()
{
    if(userName.text.toString().isEmpty()){
        userName.error = "Please enter user name/email"
        userName.requestFocus()
        return

    }
    auth.sendPasswordResetEmail(userName.text.toString())
        .addOnCompleteListener(this, OnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(this, "Unable to send reset mail", Toast.LENGTH_LONG)
                    .show()
            }
        })
}
private fun signinuser()
{
    if(userName.text.toString().isEmpty()){
        userName.error = "Please enter user name/email"
        userName.requestFocus()
        return

    }
    if(password.text.toString().isEmpty()){
        password.error ="Please enter password"
        password.requestFocus()
        return
    }
    auth.signInWithEmailAndPassword(userName.text.toString(), password.text.toString())
        .addOnCompleteListener(this, OnCompleteListener { task ->
            btn_click_me2.isEnabled = true
            if (task.isSuccessful) {
                Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()

                val intent = Intent(this, Home::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
            }
        })

}
}