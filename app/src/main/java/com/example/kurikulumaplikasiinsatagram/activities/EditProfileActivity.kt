package com.example.kurikulumaplikasiinsatagram.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kurikulumaplikasiinsatagram.MainActivity
import com.example.kurikulumaplikasiinsatagram.R
import com.example.kurikulumaplikasiinsatagram.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var cekProfileInfo = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePP: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storagePP = FirebaseStorage.getInstance().reference.child("Image")

        btn_DontSave.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        change_picture.setOnClickListener {
            cekProfileInfo = "Clicked"

            CropImage.activity()
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
        }

        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val pergi = Intent(this, LoginActivity::class.java)
            pergi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(pergi)
            finish()
        }

        btn_save.setOnClickListener {
            if (cekProfileInfo == "Clicked") {
                updateProfilePicture()

            } else {
                UpdateUserInfo()
            }
        }

        userInfo()
    }

    private fun updateProfilePicture() {
        when {
            imageUri == null -> {
                Toast.makeText(this, "must be filled", Toast.LENGTH_SHORT).show()
            }

            TextUtils.isEmpty(set_Username.text.toString()) -> {
                Toast.makeText(this, "must be filled", Toast.LENGTH_SHORT).show()
            }

            TextUtils.isEmpty(set_bio.text.toString()) -> {
                Toast.makeText(this, "must be filled", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Updating profile")
                progressDialog.setMessage("Please wait")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val fireRef = storagePP?.child(firebaseUser.uid + "jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fireRef!!.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (task.isSuccessful) {
                        task.exception.let { it ->
                            //ini dihapus
//                            throw it!!
                        }
                    }
                    return@Continuation fireRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val userRef = FirebaseDatabase.getInstance().reference.child("users")
                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = set_fullname.text.toString()
                        userMap["username"] = set_Username.text.toString()
                        userMap["bio"] = set_bio.text.toString()
                        userMap["image"] = myUrl


                        userRef.child(firebaseUser.uid).updateChildren(userMap)
                        Toast.makeText(this, "Info has been updated", Toast.LENGTH_SHORT).show()
                        startActivity(
                            Intent(this, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                        progressDialog.dismiss()
                        finish()
                    }
                })
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profileImage_EDITPTOFILE.setImageURI(imageUri)
        }
    }

    private fun UpdateUserInfo() {
        when {
            TextUtils.isEmpty(set_fullname.text.toString()) -> {
                Toast.makeText(this, "must be filled", Toast.LENGTH_SHORT).show()
            }

            TextUtils.isEmpty(set_Username.text.toString()) -> {
                Toast.makeText(this, "must be filled", Toast.LENGTH_SHORT).show()
            }

            TextUtils.isEmpty(set_bio.text.toString()) -> {
                Toast.makeText(this, "must be filled", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val userRef = FirebaseDatabase.getInstance().reference
                    .child("users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = set_Username.text.toString()
                userMap["username"] = set_Username.text.toString()
                userMap["bio"] = set_bio.text.toString()

                userRef.child(firebaseUser.uid).updateChildren(userMap)
                Toast.makeText(this, "Info has been updated", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    set_fullname.setText(user?.getFullname())
                    set_Username.setText(user?.getUsername())
                    set_bio.setText(user?.getBio())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}