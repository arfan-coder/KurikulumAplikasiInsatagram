package com.example.kurikulumaplikasiinsatagram.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurikulumaplikasiinsatagram.MainActivity
import com.example.kurikulumaplikasiinsatagram.R
import com.example.kurikulumaplikasiinsatagram.adapters.CommentAdapter
import com.example.kurikulumaplikasiinsatagram.model.Comments
import com.example.kurikulumaplikasiinsatagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.list_comment.*

class CommentActivity : AppCompatActivity() {
    private var postId = ""
    private var publisherId = ""

    private var firebase: FirebaseUser? = null

    private var recycler : RecyclerView?= null
    private var commentListData : MutableList<Comments>? = null
    private var commentAdapter : CommentAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        firebase = FirebaseAuth.getInstance().currentUser

        recycler = findViewById(R.id.recycler_COMMENT)
        val linearManager = LinearLayoutManager(this)
        linearManager.reverseLayout = true
        recycler!!.layoutManager = linearManager
        recycler!!.adapter = commentAdapter

        commentListData = ArrayList()

        val intent = intent
        postId = intent.getStringExtra("postId")
        publisherId = intent.getStringExtra("publisherId")

        post_COMMENT.setOnClickListener {
            if (et_COMMENT.text.toString() == ""){
                Toast.makeText(this,"cannot post empty texts",Toast.LENGTH_SHORT).show()
            }else{
                addComent()
            }
        }

        getPostImage()
        getCommenterInfo()
        addComent()
        readComments()
    }

    private fun getPostImage() {
        val postRef = FirebaseDatabase.getInstance().reference.child("posts").child(postId).child("postImage")

        postRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val image = snapshot.value.toString()

                    Picasso.get()
                        .load(image)
                        .into(Post_COMMENT)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun readComments() {
        val commentRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)

        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    commentListData!!.clear()

                    for (s in snapshot.children){
                        val comment = s.getValue(Comments::class.java)
                        commentListData!!.add(comment!!)
                    }
//                    commentAdapter?.notifyDataSetChanged()
                    commentAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addComent() {
        val commentRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)

        val commentMap = HashMap<String,Any>()
        commentMap["comments"] = et_COMMENT.text.toString()
        commentMap["publisher"] = firebase!!.uid

        commentRef.push().setValue(commentMap)
        et_COMMENT.text.clear()
    }

    private fun getCommenterInfo() {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(firebase!!.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get()
                        .load(user!!.getImage())
                        .into(circleImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}