package com.example.kurikulumaplikasiinsatagram.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurikulumaplikasiinsatagram.activities.EditProfileActivity
import com.example.kurikulumaplikasiinsatagram.R
import com.example.kurikulumaplikasiinsatagram.adapters.ProfilePostAdapter
import com.example.kurikulumaplikasiinsatagram.model.Posts
import com.example.kurikulumaplikasiinsatagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {
    private lateinit var profileId : String
    private lateinit var firebaseUser : FirebaseUser

    private var postGridList : MutableList<Posts>? = null
    private var imageAdapter : ProfilePostAdapter? = null
    private var recycler : RecyclerView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val viewprofile =  inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        recycler = viewprofile.findViewById(R.id.recycler_PROFILE)
        recycler!!.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context,3)
        layoutManager.reverseLayout = true
        recycler!!.layoutManager = layoutManager

        postGridList = ArrayList()
        imageAdapter = context!!.let { ProfilePostAdapter(it, postGridList as ArrayList<Posts>) }
        recycler!!.adapter = imageAdapter

        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("profileid","none")!!
        }

        if (profileId == firebaseUser.uid){
            view?.btn_edit__profile?.text = "Edit Profile"
        }else if (profileId != firebaseUser.uid){
            cekFollowingButtonStatus()
        }

        viewprofile.btn_edit__profile.setOnClickListener {
            val getButtonOntext = view?.btn_edit__profile?.text?.toString()

            when{
                getButtonOntext == "Edit Profile"-> {
                    startActivity(Intent(context, EditProfileActivity::class.java))
                }

                getButtonOntext == "Follow" -> {
                    firebaseUser.uid.let {it->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Following").child(profileId).setValue(true)
                    }
                    firebaseUser.uid.let {it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it.toString()).setValue(true)
                    }
                }

                getButtonOntext == "Following" ->{
                    firebaseUser.uid.let {it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Following").child(it.toString()).removeValue()
                    }
                }

            }
        }

        userInfo()
        getFollowing()
        getFollowers()
        myPost()
        return viewprofile
    }

    private fun myPost() {
        val firebase = FirebaseDatabase.getInstance().reference.child("posts")

        firebase.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (postGridList as ArrayList<Posts>).clear()

                    for (s in snapshot.children){
                        val post = s.getValue(Posts::class.java)
                        if (post!!.getPublisher() == profileId){
                            (postGridList as ArrayList<Posts>).add(post)
                        }
                        postGridList!!.reverse()
                        imageAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)

//                    Picasso.get()
//                        .load(user?.getImage())
//                        .into(view?.img_Profile)

                    tv_Username_Name?.text = user?.getUsername()
                    tv_fullName_profile?.text = user?.getFullname()
                    tv_bio_profile?.text = user?.getBio()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getFollowing() {
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")
        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    view?.txt_following?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun cekFollowingButtonStatus() {
        val followingref = firebaseUser.uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }

        followingref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    view?.btn_edit__profile?.text = "Following"
                }else{
                    view?.btn_edit__profile?.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    view?.txt_follower?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }



    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileid", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileid", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileid",firebaseUser.uid)
        pref?.apply()
    }
}