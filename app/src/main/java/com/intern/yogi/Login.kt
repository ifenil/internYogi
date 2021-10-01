package com.intern.yogi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.*
import kotlinx.coroutines.InternalCoroutinesApi


class Login : ComponentActivity() {
    private var reference: DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@Login);

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colors.background) {
                    if(sp.getBoolean("islogged",false)){
                        val intent = Intent(this@Login,Main::class.java)
                        startActivity(intent)
                    }else{
                        login()
                    }
                }
            }
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun logged(username:String, password:String){
        reference = FirebaseDatabase.getInstance().getReference("users")

        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@Login);
        val checkuser: Query = reference!!.orderByKey().equalTo(username)

        checkuser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val passwordFromDB: String = java.lang.String.valueOf(
                        snapshot.child(username).child("password").getValue()
                    )
                    if (password == passwordFromDB) {
                        sp.edit().putBoolean("islogged", true).apply()
                        Toast.makeText(this@Login,"Logged in",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Login,Main::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@Login,"Wrong Password",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Login,"No Such User Exist",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    @Composable
    fun login() {
        val username = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back,", color = Color.Black, fontSize = 25.sp,
                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold
            )
            Text(
                text = "Login", color = Color.Black, fontSize = 25.sp,
                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                singleLine = true,
                value = username.value,
                onValueChange = {
                    username.value = it
                },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "Username")
                },
                label = {
                    Text(text = "Username")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                singleLine = true,
                value = password.value,
                onValueChange = {
                    password.value = it
                },
                leadingIcon = {
                    Icon(Icons.Default.Info, contentDescription = "Password")
                },
                label = {
                    Text(text = "Password")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(onClick = { logged(username.value, password.value) }, modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)) {
                Text(text = "Login")
            }
        }
    }
}