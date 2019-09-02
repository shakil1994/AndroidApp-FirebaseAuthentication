package com.example.shakil.kotlinfirebaseauthexample

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edtEmail
import kotlinx.android.synthetic.main.activity_login.edtPassword
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var reference: DatabaseReference
    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()

        txt_login.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        })

        btnRegister.setOnClickListener{
            dialog.show()

            val str_username = edtUsername.text.toString()
            val str_fullname = edtFullname.text.toString()
            val str_email = edtEmail.text.toString()
            val str_password = edtPassword.text.toString()

            if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname)
                || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                Toast.makeText(this@RegisterActivity, "All Fields are required", Toast.LENGTH_SHORT).show()
            }
            else if (str_password.length < 6){
                Toast.makeText(this@RegisterActivity, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            }
            else{
                register(str_username, str_fullname, str_email, str_password)
            }
        }
    }

    private fun register(strUsername: String, strFullname: String, strEmail: String, strPassword: String) {
        auth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(this@RegisterActivity){
            task ->
            if (task.isSuccessful){
                val  firebaseUser = auth.currentUser
                val userid = firebaseUser!!.uid

                reference = FirebaseDatabase.getInstance().reference.child("Users").child(userid)

                val hashMap = HashMap<String, Any>()
                hashMap["id"] = userid
                hashMap["username"] = strUsername.toLowerCase()
                hashMap["fullname"] = strFullname

                reference.setValue(hashMap).addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        dialog.dismiss()

                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }
            else{
                dialog.dismiss()
                Toast.makeText(this@RegisterActivity, "You can't register with this email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
