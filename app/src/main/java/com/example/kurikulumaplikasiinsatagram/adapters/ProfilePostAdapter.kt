package com.example.kurikulumaplikasiinsatagram.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kurikulumaplikasiinsatagram.R
import com.example.kurikulumaplikasiinsatagram.fragments.DetailProfileFragment
import com.example.kurikulumaplikasiinsatagram.model.Posts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class ProfilePostAdapter(private val mContext : Context,private val mImage : List<Posts>):
    RecyclerView.Adapter<MyPostHolder>(){

    private var firebaseUser : FirebaseUser?= FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_post_profile,parent,false)
        return MyPostHolder(view)
    }

    override fun onBindViewHolder(holder: MyPostHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post = mImage[position]

        Picasso.get()
            .load(post.getPostImage())
            .into(holder.postImage)

        holder.itemView.setOnClickListener {
            val gotoDetail = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            gotoDetail.putString("postId",post.getPostId())
            gotoDetail.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.frame,DetailProfileFragment()).commit()
        }
    }

    override fun getItemCount(): Int {
        return mImage.size
    }

}

class MyPostHolder(itemview : View):RecyclerView.ViewHolder(itemview) {
    var postImage : ImageView = itemview.findViewById(R.id.img_Post_PROFILE)
}
