package com.example.jobmart

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.jobmart.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_post.view.*
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


        fun bind(post: Post) {
            itemView.tvUsername.text = post.user?.username
            itemView.tvAddress.text = post.address
            itemView.tvDescription.text = post.description
            itemView.tvpay.text = post.pay
            Glide.with(context).load(post.imageUrl).into(itemView.ivPost)

            itemView.tvRelativeTime.text = DateUtils.getRelativeTimeSpanString(post.creationTimeMs)

          itemView.message_button.setOnClickListener{
                    val intent = Intent(itemView.context, ChatLogActivity::class.java)
                    Log.i(TAG, "user to: ${post.userid}")
                    intent.putExtra("usertoid", post.userid)
                    itemView.context.startActivity(intent)
                }

            itemView.tvUsername.setOnClickListener{
                val intent = Intent(itemView.context, ViewUserActivity::class.java)
                Log.i(TAG, "user to: ${post.userid}")
                intent.putExtra("usertoid", post.userid)
                itemView.context.startActivity(intent)

            }




        }

    }
}