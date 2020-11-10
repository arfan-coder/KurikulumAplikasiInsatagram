package com.example.kurikulumaplikasiinsatagram.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kurikulumaplikasiinsatagram.R
import com.example.kurikulumaplikasiinsatagram.fragments.ProfileFragment
import com.example.kurikulumaplikasiinsatagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_search.view.*

class SearchAdapter(private val context: Context, private val mUser:List<User>, private var isFragment: Boolean = false) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_search,parent,false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = mUser[position]
        holder.username.text = user.getUsername()
        holder.fullname.text = user.getFullname()

        Picasso.get()
            .load(user.getImage())
            .into(holder.image)

        cekfollowing(user.getUID(), holder.buttonFollow)

//        ketika di klik pergi ke profilenya
        holder.itemView.setOnClickListener {
            val gotoProfile = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            gotoProfile.putString("profileid", user.getUID())
            gotoProfile.apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.frame,ProfileFragment()).commit()
        }

//        ketika di klik button follow (hati) kita bisa liat post baru
        holder.buttonFollow.setOnClickListener {
            if (holder.buttonFollow.text.toString() == "Follow"){

                firebaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(user.getUID())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                firebaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener {task ->
                                            if (task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            }else{
                firebaseUser?.uid.let {it ->
                    FirebaseDatabase.getInstance().getReference()
                        .child("Follow").child(it.toString())
                        .child("Following").child(user.getUID())
                        .removeValue()
                        .addOnCompleteListener {task ->
                            firebaseUser?.uid.let { it ->
                                FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(user.getUID())
                                    .child("Followers").child(it.toString())
                                    .removeValue()
                                    .addOnCompleteListener {task ->
                                        if (task.isSuccessful){

                                        }
                                    }
                            }
                        }
                }
            }
        }
    }

//    button harus huruf besar
    private fun cekfollowing(uid: String, buttonFollow: Button) {
        val following = firebaseUser?.uid.let {it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }

        following.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(uid).exists()){
                    buttonFollow.text = "Following"
                }else{
                    buttonFollow.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    inner class SearchViewHolder(itemView : View):RecyclerView.ViewHolder(itemView) {
        val username : TextView = itemView.findViewById(R.id.txt_username_SEARCH)
        val fullname : TextView = itemView.findViewById(R.id.txt_fullname_SEARCH)
        val buttonFollow : Button = itemView.findViewById(R.id.btn_follow_SEARCH)
        val image : ImageView = itemView.findViewById(R.id.img_profile_SEARCH)

    }

}
