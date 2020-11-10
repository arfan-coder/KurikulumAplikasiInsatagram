package com.example.kurikulumaplikasiinsatagram.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.kurikulumaplikasiinsatagram.MainActivity
import com.example.kurikulumaplikasiinsatagram.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_back_REGISTER.setOnClickListener {
            val goToLogin = Intent(this, LoginActivity::class.java)
            startActivity(goToLogin)
        }

        txt_Login_REGISTER.setOnClickListener {
            val goToLogin = Intent(this, LoginActivity::class.java)
            startActivity(goToLogin)
        }

        btn_REGISTER.setOnClickListener {
            createAkun()
        }
    }

    private fun createAkun() {
        val fullname = editText.text.toString()
        val username = editText2.text.toString()
        val email = editText3.text.toString()
        val password = editText4.text.toString()

        when{
            TextUtils.isEmpty(fullname) -> Toast.makeText(this,"Fullname needs to be filled",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(username) -> Toast.makeText(this,"Username needs to be filled",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this,"Email needs to be filled",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"Password needs to be filled",Toast.LENGTH_SHORT).show()
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Register")
                progressDialog.setMessage("please wait...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful){
                            saveUserInfo(fullname, username, email, password, progressDialog)
                        }
                }
            }
        }
    }

    private fun saveUserInfo(fullname: String, username: String, email: String, password: String, progressDialog : ProgressDialog) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
        val userMap = HashMap<String, Any>()

//        set default
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullname
        userMap["username"] = username
        userMap["email"] = email
        userMap["bio"] = "Hey! I am a Student"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/kurikulumaplikasiinstagram.appspot.com/o/Image%2FMyPhoto.jpg?alt=media&token=03f90068-cf00-4664-8809-09c386a1d58a"

//        terjadi di RegisterActivity  masukin dan save user yang sudah meregister dan lansung ke Main
        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this, "account has been made",Toast.LENGTH_SHORT).show()

                    val goToMain = Intent(this, MainActivity::class.java)
                    goToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(goToMain)
                    finish()
                }else{
                    val message = task.exception!!.toString()
                    Toast.makeText(this,"Error $message", Toast.LENGTH_SHORT)
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }
}