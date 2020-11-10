package com.example.kurikulumaplikasiinsatagram.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kurikulumaplikasiinsatagram.activities.CommentActivity
import com.example.kurikulumaplikasiinsatagram.R
import com.example.kurikulumaplikasiinsatagram.model.Posts
import com.example.kurikulumaplikasiinsatagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val mContext:Context, private val mPost: List<Posts>): RecyclerView.Adapter<MyViewHolder>() {
    private var firebaseUser : FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_post,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

//        displaying post in HomeScreen
        val myPost = mPost[position]
        Picasso.get()
            .load(myPost.getPostImage())
            .into(holder.imagePost)
        if (myPost.getDescription().equals("")){
            holder.description.visibility = View.GONE
        }else{
            holder.description.visibility = View.VISIBLE
            holder.description.text = myPost.getDescription()
        }

        getTotalComment(holder.sum_comments, myPost.getPostId())

        publisherInfo(holder.profileImage, holder.username, holder.publisher_name, myPost.getPublisher())
        ngelike(myPost.getPostId(),holder.like)
        numberOfLikes(holder.sum_Likes, myPost.getPostId())

//        Comments
        holder.comments.setOnClickListener {
            val moveToComent = Intent(mContext, CommentActivity::class.java)
            moveToComent.putExtra("postId", myPost.getPostId())
            moveToComent.putExtra("publisherId",myPost.getPublisher())
            mContext.startActivity(moveToComent)
        }

//        likes
        holder.like.setOnClickListener {
            if (holder.like.tag == "Like"){
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(myPost.getPostId()).child(firebaseUser!!.uid)
                    .setValue(true)
            }else{
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(myPost.getPostId()).child(firebaseUser!!.uid)
                    .removeValue()
            }
        }
    }

    private fun getTotalComment(sumComments: TextView, postId: String) {
        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments")
        commentsRef.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    sumComments.text = snapshot.childrenCount.toString() + " comments"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun numberOfLikes(like: TextView, postId: String) {
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        likesRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                    like.text = snapshot.childrenCount.toString() + " Likes"

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun ngelike(postId: String, like: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(postId)

        likesRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()){
                    like.setImageResource(R.drawable.red_heart)
                    like.tag = "Liked"
                }else{
                    like.setImageResource(R.drawable.heart)
                    like.tag ="Like"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun publisherInfo(profileImage: CircleImageView, username: TextView, publisherName: TextView, publisher: String){
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(publisher)

        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>(User::class.java)

                Picasso.get()
                    .load(user?.getImage())
                    .into(profileImage)
                username.text = user?.getUsername()
                publisherName.text = user?.getFullname()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun getItemCount(): Int {
        return mPost.size
    }
}

class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView) {
//    posts
    var profileImage : CircleImageView = itemView.findViewById(R.id.img_detail_Post)
    var username : TextView = itemView.findViewById(R.id.txt_name_Post)
    var imagePost : ImageView = itemView.findViewById(R.id.imageView4)

    //UI
    var like : ImageView = itemView.findViewById(R.id.img_Heart_HOME)
    var comments : ImageView = itemView.findViewById(R.id.img_Comment_HOME)
    var share : ImageView = itemView.findViewById(R.id.img_Send_HOME)
    var save : ImageView = itemView.findViewById(R.id.img_Save_HOME)

//    comments
    var sum_Likes : TextView = itemView.findViewById(R.id.likes_HOME)
    var publisher_name : TextView = itemView.findViewById(R.id.publisher_HOME)
    var description : TextView = itemView.findViewById(R.id.description_HOME)
    var sum_comments : TextView = itemView.findViewById(R.id.comments_HOME)
}
