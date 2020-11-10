package com.example.kurikulumaplikasiinsatagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.kurikulumaplikasiinsatagram.activities.AddPostActivity
import com.example.kurikulumaplikasiinsatagram.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        setDefaultView -> HomeFragment
        var frag = supportFragmentManager.beginTransaction()
        frag.add(R.id.frame,HomeFragment())
        frag.commit()

        bottomNav.setOnNavigationItemSelectedListener(botNavListener)
    }

    private val botNavListener = BottomNavigationView.OnNavigationItemSelectedListener { a->
        var selectedFragment : Fragment = HomeFragment()

        when(a.itemId){
            R.id.home ->{
                selectedFragment = HomeFragment()
            }
            R.id.addPost -> {
                startActivity(Intent(this, AddPostActivity::class.java))
            }
            R.id.profile->{
                selectedFragment = ProfileFragment()
            }
            R.id.search->{
                selectedFragment = SearchFragment()
            }
            R.id.activity->{
                selectedFragment = ActivityFragment()
            }
        }

        var frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.frame,selectedFragment)
        frag.commit()

        true
    }
}