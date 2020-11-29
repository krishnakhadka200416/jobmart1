package com.example.jobmart

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jobmart.models.Post
import com.example.jobmart.models.User
import com.example.jobmart.models.UserProfile
import kotlinx.android.synthetic.main.item_post.view.*

class UserinfoAdapter(val context: Context, val userProfile: List<User>):

