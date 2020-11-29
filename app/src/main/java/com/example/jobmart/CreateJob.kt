package com.example.jobmart

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.jobmart.models.Post
import com.example.jobmart.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_job.*

private const val TAG = "CreateJob"
private  const val PICK_PHOTO_CODE = 1234
private lateinit var firestoreDb : FirebaseFirestore
private lateinit var storageReference : StorageReference


class CreateJob : AppCompatActivity() {
    private var signedInUser: User? = null
    private var photoUri: Uri?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_job)
        storageReference = FirebaseStorage.getInstance().reference
        firestoreDb = FirebaseFirestore.getInstance()
        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "signed in user : $signedInUser")

            }
            .addOnFailureListener{exception ->
                Log.i(TAG, "Failure fetching signed in user", exception)

            }

        btnPickImage.setOnClickListener {
            Log.i(TAG,"Open up image picker on device")
            val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"

            if(imagePickerIntent.resolveActivity(packageManager)!= null){
                    startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE)
                }
        }
        btnpost.setOnClickListener {
            handleSubmitButtonClick()
        }
    }
    private fun handleSubmitButtonClick(){
        if(photoUri == null){
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show()
            return
        }
        if(etDescription.text.isBlank() == null){
            Toast.makeText(this, "No Description", Toast.LENGTH_SHORT).show()
            return
        }
        if(etAddress.text.isBlank() == null){
            Toast.makeText(this, "No Address", Toast.LENGTH_SHORT).show()
            return
        }
        if(radiocheck.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "No Type checked", Toast.LENGTH_SHORT).show()
            return
        }
        if(signedInUser == null)
        {
            Toast.makeText(this, "There is no signed in user", Toast.LENGTH_SHORT).show()
            return
        }
        btnpost.isEnabled = false
        val photoUploadUri = photoUri as Uri
        //give reference to each image to make it unique
        val photoReference = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
        photoReference.putFile(photoUploadUri)
            .continueWithTask{ photoUploadTask ->
                Log.i(TAG, "uploaded bytes: ${photoUploadTask.result?.bytesTransferred}")
                photoReference.downloadUrl
            }.continueWithTask { downloadUrlTask ->
                val post = Post(
                    etDescription.text.toString(),
                    downloadUrlTask.result.toString(),
                    System.currentTimeMillis(),
                    etAddress.text.toString(),
                    signedInUser)
                firestoreDb.collection("posts").add(post)
            }.addOnCompleteListener{postCreationTask ->
                btnpost.isEnabled = true
                if(!postCreationTask.isSuccessful){
                    Log.e(TAG, "Exception during Firebase operations", postCreationTask.exception)
                    Toast.makeText(this, "Failed to save post", Toast.LENGTH_SHORT).show()

                }
                etDescription.text.clear()
                imageView.setImageResource(0)
                Toast.makeText(this, "Success!",Toast.LENGTH_SHORT).show()
                val profileIntent = Intent(this, ProfileActivity::class.java)
                profileIntent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                startActivity(profileIntent)
                finish()
            }




    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_PHOTO_CODE){
            if (resultCode == Activity.RESULT_OK){
                photoUri =data?.data
                Log.i(TAG, "photoUri $photoUri")
                imageView.setImageURI((photoUri))

            }
            else{
                Toast.makeText(this, "Image picker canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}