package com.example.shakil.kotlinfirebaseauthexample

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()

        txt_signup.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        })

        btnLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                dialog.show()

                val str_email = edtEmail.text.toString()
                val str_password = edtPassword.text.toString()

                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(this@LoginActivity, "All Fields are required", Toast.LENGTH_SHORT).show()
                }
                else{
                    auth.signInWithEmailAndPassword(str_email, str_password)
                        .addOnCompleteListener (this@LoginActivity){
                            task ->
                            if (task.isSuccessful){
                                val reference = FirebaseDatabase.getInstance().reference.child("Users")
                                    .child(auth.currentUser!!.uid)

                                reference.addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        dialog.dismiss()
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        dialog.dismiss()

                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        startActivity(intent)
                                        finish()
                                    }

                                })
                            }
                            else{
                                dialog.dismiss()
                                Toast.makeText(this@LoginActivity, "Your email or password doesn't match", Toast.LENGTH_SHORT).show()

                            }
                        }
                }
            }

        })
    }
}
