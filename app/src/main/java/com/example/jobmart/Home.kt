package com.example.jobmart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobmart.models.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_home.*

private const val TAG = "Home"
class Home : AppCompatActivity() {
    private lateinit var  firestoreDb: FirebaseFirestore
    private lateinit var posts:MutableList<Post>
    private lateinit var adapter: PostsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        posts = mutableListOf()
        adapter = PostsAdapter(this, posts)
        rvPosts.adapter = adapter
        rvPosts.layoutManager = LinearLayoutManager(this)
        firestoreDb = FirebaseFirestore.getInstance()
        val postReference = firestoreDb
            .collection("posts")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)
        postReference.addSnapshotListener{snapshot , exception->
            if(exception != null || snapshot == null)
            {
                Log.e(TAG, "Exception when querrying post", exception)
                return@addSnapshotListener
            }
           val postlist= snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postlist)
            adapter.notifyDataSetChanged()
            for(post in postlist){
                Log.i(TAG, "Post ${post}")
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== R.id.menu_profile){
            val intent = Intent(this, ProfileActivity::class.java )
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}