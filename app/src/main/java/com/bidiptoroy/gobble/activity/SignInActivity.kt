package com.bidiptoroy.gobble.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class SignInActivity : AppCompatActivity()
{
    lateinit var userNumberEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var loginButton: Button
    lateinit var forgotPWDButton: Button
    lateinit var btnSignUp: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // initialising views
        userNumberEditText = findViewById(R.id.userNumber)
        passwordEditText = findViewById(R.id.userPassword)
        loginButton = findViewById(R.id.login)
        forgotPWDButton =findViewById(R.id.forgotPassword)
        btnSignUp = findViewById(R.id.signUp)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        if(sharedPreferences.getBoolean("isLoggedIn",false)){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        btnSignUp.setOnClickListener {
            val intent = Intent(this,RegisterActivity :: class.java)
            startActivity(intent)
        }
        forgotPWDButton.setOnClickListener {
            val intent = Intent(this,ForgotPWDActivity ::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {

            if(ConnectionManager().checkConnectivity(this)){

                val queue = Volley.newRequestQueue(this)
                val logInUrl = "http://13.235.250.119/v2/login/fetch_result"
                val jsonparams = JSONObject()
                jsonparams.put("mobile_number",userNumberEditText.text.toString())
                jsonparams.put("password",passwordEditText.text.toString())

              val jsonObjectRequest = object : JsonObjectRequest(Method.POST,logInUrl,jsonparams,Response.Listener {

                  try{
                      val data = it.getJSONObject("data")
                      val success = data.getBoolean("success")
                      if(success){
                          Log.e("response","success"+it)
                          val response = data.getJSONObject("data")
                          sharedPreferences.edit().putString("user_id",response.getString("user_id")).apply()
                          sharedPreferences.edit().putString("name",response.getString("name")).apply()
                          sharedPreferences.edit().putString("email",response.getString("email")).apply()
                          sharedPreferences.edit().putString("mobile_number",response.getString("mobile_number")).apply()
                          sharedPreferences.edit().putString("address",response.getString("address")).apply()
                          sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                          startActivity(Intent(this,MainActivity ::class.java))
                          finish()

                      }else{
                          val errorMessage = data.getString("errorMessage")
                          Toast.makeText(
                              this,
                              errorMessage,
                              Toast.LENGTH_SHORT
                          ).show()
                      }
                  }catch (e: JSONException) {
                      Log.e("response", "error is $it")
                      Toast.makeText(
                          this,
                          "Some unexpected error occurred",
                          Toast.LENGTH_SHORT
                      ).show()
                  }

              },Response.ErrorListener {

              }){
                  override fun getHeaders(): MutableMap<String, String>
                  {
                      val headers = HashMap<String, String>()
                      headers["Content-type"] = "application/json"
                      headers["token"] = "dd2d16fde48094"
                      return headers
                  }

              }
                queue.add(jsonObjectRequest)
            }else{
                val dialog =  AlertDialog.Builder(this)
                dialog.setTitle("Some Problem occurred")
                    .setMessage("No Internet Connection ")
                    .setPositiveButton("Open Settings"){text, listener ->

                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()

                    }
                    .setNegativeButton("Exit"){text, listener ->
                        ActivityCompat.finishAffinity(this)
                    }
                    .create().show()
            }


        }


    }


}