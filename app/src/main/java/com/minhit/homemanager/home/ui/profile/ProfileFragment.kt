package com.minhit.homemanager.home.ui.profile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.minhit.homemanager.R
import com.minhit.homemanager.database.DatabaseHelper
import com.minhit.homemanager.model.UserModel

class ProfileFragment : Fragment() ,DatabaseHelper{

    lateinit var txtManagerName:TextView
    lateinit var txtEmail:TextView
    lateinit var txtPassword:TextView
    lateinit var editName:ImageView
    lateinit var editEmail:ImageView
    lateinit var editPassword:ImageView
    lateinit var btnLogOut:Button
    var user:UserModel = UserModel()
    lateinit var listener:SignOutAccount



    private lateinit var database: DatabaseReference
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        addControls(view)
        getData()
        setData()
        signOut()
        return view
    }

    private fun signOut() {
        btnLogOut.setOnClickListener {
            listener.clickSignOut()
        }

    }


    private fun addControls(view: View) {
        txtManagerName = view.findViewById(R.id.txtManagerName)
        txtEmail = view.findViewById(R.id.txtEmailUser)
        txtPassword = view.findViewById(R.id.txtPasswordUser)
        editName = view.findViewById(R.id.imgEditManagerName)
        editEmail = view.findViewById(R.id.imgEditEmail)
        editPassword = view.findViewById(R.id.imgEditPassword)
        btnLogOut = view.findViewById(R.id.btnLogout)
        database = Firebase.database.getReference("UserHomeManager/${Firebase.auth.currentUser!!.uid}")


    }

    override fun getData() {
        val getUser = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onDataChange(p0: DataSnapshot) {
                val u = p0.getValue<UserModel>()!!
                txtManagerName.text = "Name : ${u.name}"
                txtEmail.text = "Email : ${u.email}"
                if(p0.exists()){
                    user = u
                }

            }

        }

        database.addValueEventListener(getUser)



    }

    override fun setData() {
        clickEdit(editName)
        clickEdit(editEmail)
        clickEdit(editPassword)
    }

    private fun clickEdit(img: ImageView) {
        img.setOnClickListener {
            val dialog = this!!.activity?.let { it1 -> Dialog(it1) }
            dialog!!.setContentView(R.layout.dialog_edit_user)
            dialog!!.setCancelable(false)
            var title = dialog!!.findViewById<TextView>(R.id.txtTitleEdit)
            var content = dialog!!.findViewById<EditText>(R.id.edtEditUser)
            var save = dialog!!.findViewById<Button>(R.id.btnSaveEdit)
            var cancel = dialog!!.findViewById<Button>(R.id.btnCancelEdit)
            when(img){
                editName->{
                    title.text = "Edit name"
                    content.setHint("Name")
                    content.setText("${user.name}")

                }
                editEmail->{
                    title.text = "Edit email"
                    content.setHint("Email")
                    content.setText("${user.email}")
                }
                editPassword->{
                    title.text = "Edit password"
                    content.setHint("Password")
                    content.setText("${user.password}")
                }
            }
            save.setOnClickListener {
                if(content.text.toString().trim().isEmpty()){
                    showToast("Content is empty.")
                }else{
                    when(img){
                        editName->{
                            user.name= content.text.toString().trim()
                            database.child("name").setValue("${user.name}")
                                .addOnSuccessListener {
                                    showToast("Edit success")
                                    dialog.dismiss()
                                }
                                .addOnFailureListener {
                                    showToast("Edit fail")
                                }
                        }
                        editEmail->{
                            user.email= content.text.toString().trim()
                            val auth = Firebase.auth.currentUser
                            auth!!.updateEmail("${user.email}")
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        auth.sendEmailVerification()
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    showToast("You need confirm email.")
                                                    database.child("email").setValue("${user.email}")
                                                        .addOnSuccessListener {
                                                            showToast("Edit success")
                                                            dialog.dismiss()
                                                        }
                                                        .addOnFailureListener {
                                                            showToast("Edit fail")
                                                        }
                                                }

                                            }
                                    }
                                }
                        }
                        editPassword->{
                            user.password= content.text.toString().trim()
                            val auth = Firebase.auth.currentUser
                            auth!!.updatePassword("${user.password}")
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        database.child("password").setValue("${user.password}")
                                            .addOnSuccessListener {
                                                showToast("Edit success")
                                                dialog.dismiss()
                                            }
                                            .addOnFailureListener {
                                                showToast("Edit fail")
                                            }
                                    }
                                }
                        }
                    }
                }
            }

            cancel.setOnClickListener {dialog.dismiss()}

            dialog!!.show()
        }
    }

    private fun showToast(s: String) {
        Toast.makeText(activity,s,Toast.LENGTH_SHORT).show()
    }

    interface SignOutAccount{
        fun clickSignOut()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SignOutAccount) {
            listener = context
        } else {
            throw ClassCastException(
                context.toString() + " must implement OnDogSelected.")
        }
    }

}