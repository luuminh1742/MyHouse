package com.minhit.homemanager.login

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.minhit.homemanager.R
import com.minhit.homemanager.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        addControls()
    }



    private fun addControls() {
        auth = Firebase.auth
        progressBar.visibility = View.INVISIBLE

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    fun clickLogIn(view: View) {
        if(edtEmailLogin.text.toString().trim().isEmpty()||
                edtPasswordLogin.text.toString().trim().isEmpty()){
            showToast("Yout need check information again.")
        }else{
            progressBar.visibility = View.VISIBLE
            val email:String = edtEmailLogin.text.toString().trim()
            val password:String = edtPasswordLogin.text.toString().trim()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if(auth.currentUser!!.isEmailVerified){
                            startActivity(Intent(this,HomeActivity::class.java))
                            progressBar.visibility = View.INVISIBLE
                            finish()
                        }else{
                            progressBar.visibility = View.INVISIBLE
                            showToast("You need confirm email.")
                        }

                    } else {
                        progressBar.visibility = View.INVISIBLE
                        showToast("Log in fail")
                    }
                }
        }




    }
    fun clickSignup(view: View) {
        startActivity(Intent(this,SignupActivity::class.java))
    }
    fun showToast(s:String){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
    }



    private val nameShare = "SaveLogIn"

    override fun onPause() {
        super.onPause()
        val preferences = getSharedPreferences(nameShare, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("email", edtEmailLogin.getText().toString())
        editor.putString("password", edtPasswordLogin.getText().toString())
        editor.putBoolean("save", chkRememberAccount.isChecked())
        editor.commit()
    }

    override fun onResume() {
        super.onResume()
        val preferences =
            getSharedPreferences(nameShare, Context.MODE_PRIVATE)
        val email = preferences.getString("email", "")
        val password = preferences.getString("password", "")
        val save = preferences.getBoolean("save", false)
        if (save) {
            edtEmailLogin.setText(email)
            edtPasswordLogin.setText(password)
        }
        chkRememberAccount.setChecked(save)
    }

    fun clickFrogotPassword(view: View) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_email_resert_password)

        var email = dialog.findViewById<EditText>(R.id.edtEmailResert)
        var btnSend = dialog.findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {
            if(email.text.toString().trim().isEmpty()){
                showToast("Check email again")
            }else{
                val auth = FirebaseAuth.getInstance()
                val emailAddress: String = email.getText().toString().trim()
                auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("Check your email to reset your password")
                            dialog.dismiss()
                        } else {
                            showToast("Email failed to send")
                        }
                    }
            }
        }

        dialog.show()
    }
}