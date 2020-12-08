package com.example.jobmart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmart.models.Post
import com.example.jobmart.models.User
import com.google.common.collect.Iterables.limit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.rvPosts
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_sign_in.*

private const val TAG = "SearchActivity"
class SearchActivity : AppCompatActivity() {
    private var signedInUser: User? = null
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val searchtext: String

        posts = mutableListOf()
        adapter = PostsAdapter(this, posts)
        rvPosts.adapter = adapter
        rvPosts.layoutManager = LinearLayoutManager(this)

        searchtext = search_text.text.toString().toLowerCase()
        supportActionBar?.title = "Search"

        firestoreDb = FirebaseFirestore.getInstance()

        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "signed in user : $signedInUser")

            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Failure fetching signed in user", exception)

            }


        search_button1.setOnClickListener{

            var postReference = firestoreDb
                .collection("posts")
                .whereEqualTo("address", search_text.text.toString())

                .limit(20)
                .orderBy("creation_time_ms", Query.Direction.DESCENDING)
            val username = intent.getStringExtra(EXTRA_USERNAME)

            if (username != null) {
                supportActionBar?.title = username
                postReference = postReference.whereEqualTo("user.username", username)
            }
            postReference.addSnapshotListener { snapshot, exception ->
                if (exception != null || snapshot == null) {
                    Log.e(TAG, "Exception when querrying post", exception)
                    return@addSnapshotListener
                }
                val postlist = snapshot.toObjects(Post::class.java)
                posts.clear()
                posts.addAll(postlist)
                adapter.notifyDataSetChanged()
                for (post in postlist) {
                    Log.i(TAG, "Post ${post}")
                }

            }
        }
        search_button2.setOnClickListener{

            var postReference = firestoreDb
                .collection("posts")

                .limit(20)
                .orderBy("creation_time_ms", Query.Direction.DESCENDING)
            val username = intent.getStringExtra(EXTRA_USERNAME)

            if (username != null) {
                supportActionBar?.title = username
                postReference = postReference.whereEqualTo("user.username", username)
            }
            postReference.addSnapshotListener { snapshot, exception ->
                if (exception != null || snapshot == null) {
                    Log.e(TAG, "Exception when querrying post", exception)
                    return@addSnapshotListener
                }
                val postlist = snapshot.toObjects(Post::class.java)
                posts.clear()
                posts.addAll(postlist)
                adapter.notifyDataSetChanged()
                for (post in postlist) {
                    Log.i(TAG, "Post ${post}")
                }

            }
        }
    }
}