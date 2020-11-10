package com.example.kurikulumaplikasiinsatagram.adapters

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurikulumaplikasiinsatagram.R
import com.example.kurikulumaplikasiinsatagram.model.Comments
import com.example.kurikulumaplikasiinsatagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.list_comment.view.*
import org.w3c.dom.Comment

class CommentAdapter(private val mContext : Context, private val mComment : MutableList<Comments>):
    RecyclerView.Adapter<MyCommentHolder>(){
    private var firebaseUser : FirebaseUser?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCommentHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_comment,parent,false)
        return MyCommentHolder(view)
    }

    override fun onBindViewHolder(holder: MyCommentHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val myComment = mComment[position]

        holder.comment.text = myComment.getComment()
        getUserInfo(holder.image,holder.name,myComment.getPublisher())
    }

    private fun getUserInfo(image: CircleImageView, name: TextView, publisher: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(publisher)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)

                    Picasso.get()
                        .load(user!!.getImage())
                        .into(image)

                    name.text = user.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun getItemCount(): Int {
        return mComment.size
    }

}

class MyCommentHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
    var image : CircleImageView = itemView.findViewById(R.id.img_profile_COMMENT)
    var name : TextView = itemView.findViewById(R.id.tv_name_COMMENT)
    var comment : TextView = itemView.findViewById(R.id.tv_comments_COMMENT)
}


//    override fun onBindViewHolder(holder: MyCommentViewHolder, position: Int) {
//        firebbaseUser = FirebaseAuth.getInstance().currentUser
//        val myComment = mComment[position]
//
//        holder.comment.text = myComment.getComment()
//
//        getUserInfo(holder.image,holder.name,myComment.getPublisher())
//    }