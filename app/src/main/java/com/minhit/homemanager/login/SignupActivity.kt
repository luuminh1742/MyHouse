package com.minhit.homemanager.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.minhit.homemanager.R
import com.minhit.homemanager.model.UserModel
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {


    private var database: DatabaseReference
    private var auth: FirebaseAuth


    init {
        database = Firebase.database.reference
        auth = Firebase.auth
    }


    lateinit var userModel:UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }



    fun clickBack(view: View) {
        finish()
    }
    fun clickSingUp(view: View) {
        if(edtNewNameUser.text.toString().trim().isEmpty()||
                edtNewEmailUser.text.toString().trim().isEmpty()||
                edtNewPasswordUser.text.toString().trim().isEmpty()||
                edtConfirmPassword.text.toString().trim().isEmpty()){
            showToast("You need check infor again.")
        }else{


            userModel = UserModel(null,edtNewNameUser.text.toString(),
            edtNewEmailUser.text.toString(),edtNewPasswordUser.text.toString())
            if(userModel.password.equals(edtConfirmPassword.text.toString()) && userModel.password!!.length >= 8){

                auth.createUserWithEmailAndPassword(userModel.email, userModel.password!!)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            auth.currentUser!!.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        showToast("You need confirm email.")
                                        val user: FirebaseUser = auth.currentUser!!
                                        userModel.id = user.uid
                                        updateUI(user,userModel)
                                    }

                                }


                        } else {

                        }

                        // ...
                    }
            }else{
                showToast("You need check password again. Password is at least 8 characters")
            }
        }


    }

    private fun updateUI(
        user: FirebaseUser?,
        userModel: UserModel
    ) {
        database.child("UserHomeManager").child("${user!!.uid}").setValue(userModel)
            .addOnSuccessListener {
                showToast("Add success")
                finish()
            }
            .addOnFailureListener {
                showToast("Add fail")
            }
    }

    fun showToast(s:String){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
    }
}