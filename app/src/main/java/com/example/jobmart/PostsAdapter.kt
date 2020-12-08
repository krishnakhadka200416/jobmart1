package com.example.jobmart

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.jobmart.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_post.view.*
import java.math.BigInteger
import java.security.MessageDigest

private val TAG: String = "PostAdapter"

class PostsAdapter (val context: Context, val posts:List<Post>):

    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)

        return ViewHolder(view)

    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(posts[position])

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val uid = FirebaseAuth.getInstance().uid
        private lateinit var  firestoreDb: FirebaseFirestore


        private fun getProfileImageURl(username:String): String{
            val digest = MessageDigest.getInstance("MD5");
            val hash = digest.digest(username.toByteArray());
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return "https://www.gravatar.com/avatar/$hex?d=identicon";
        }
        fun bind(post: Post) {
            val username = post.user?.username as String
            itemView.tvUsername.text = post.user?.username
            itemView.tvAddress.text = post.address
            itemView.tvDescription.text = post.description
            itemView.tvpay.text = post.pay
            Glide.with(context).load(post.imageUrl).into(itemView.ivPost)
            Glide.with(context).load(getProfileImageURl(username)).into(itemView.ivProfileImage)

            itemView.tvRelativeTime.text = DateUtils.getRelativeTimeSpanString(post.creationTimeMs)

          itemView.message_button.setOnClickListener{
                    val intent = Intent(itemView.context, ChatLogActivity::class.java)
                    intent.putExtra("usertoid", post.userid)
                    itemView.context.startActivity(intent)
                }

            itemView.tvUsername.setOnClickListener {
                val intent = Intent(itemView.context, ViewUserActivity::class.java)
                Log.i(TAG, "user to: ${post.userid}")
                intent.putExtra("usertoid", post.userid)
                itemView.context.startActivity(intent)

            }




                }






        }

    }
