package com.example.kurikulumaplikasiinsatagram.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurikulumaplikasiinsatagram.R
import com.example.kurikulumaplikasiinsatagram.adapters.PostAdapter
import com.example.kurikulumaplikasiinsatagram.model.Posts
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail_profile.*

class DetailProfileFragment : Fragment() {
    private var adapter : PostAdapter?=null
    private var recycler : RecyclerView?=null
    private var postList : MutableList<Posts>? = null
    private lateinit var postId : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_detail_profile, container, false)

        recycler = view.findViewById(R.id.recyclerDetail_PROFILE)
        val layoutManager = LinearLayoutManager(context)
        recycler!!.layoutManager = layoutManager

        postList = ArrayList()
        adapter = context.let {it?.let {it1 ->
            PostAdapter(it1, postList as ArrayList<Posts>) } }
        recycler!!.adapter = adapter

        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)
        if (pref != null){
            this.postId = pref.getString("postId","null")!!
        }

//        img_back_detailPROFILE.setOnClickListener {
//
//            (context as FragmentActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.frame,ProfileFragment()).commit()
//        }

        getPost()
        return view
    }

    private fun getPost() {
        val postRef = FirebaseDatabase.getInstance().reference.child("posts").child(postId)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue(Posts::class.java)
                postList!!.add(post!!)
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}