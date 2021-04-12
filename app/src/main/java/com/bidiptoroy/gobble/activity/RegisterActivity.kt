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
import java.lang.reflect.Method

class RegisterActivity : AppCompatActivity() {
    lateinit var btnSignUp : Button
    lateinit var edtName:EditText
    lateinit var edtEmail:EditText
    lateinit var edtMobileNumber:EditText
    lateinit var edtDeliveryAdress:EditText
    lateinit var edtPassword:EditText
    lateinit var edtConfirmPassword :EditText
    lateinit var sharedPreferences: SharedPreferences




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        edtName=findViewById(R.id.edtName)
        edtEmail=findViewById(R.id.editTextTextEmailAddress)
        edtMobileNumber=findViewById(R.id.editTextTextMobileNumber)
        edtDeliveryAdress=findViewById(R.id.editTextDeliveryAdd)
        edtPassword=findViewById(R.id.editTextEnterPassword)
        edtConfirmPassword=findViewById(R.id.editTextConfirmPassword)
        btnSignUp = findViewById(R.id.btnRegister)

        sharedPreferences =getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)


        btnSignUp.setOnClickListener{

            if (ConnectionManager().checkConnectivity(this))
            {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/register/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("name", edtName.text.toString())
                jsonParams.put("mobile_number", edtMobileNumber.text.toString())
                jsonParams.put("address",edtDeliveryAdress.text.toString())
                jsonParams.put("password", edtPassword.text.toString())
                jsonParams.put("email", edtEmail.text.toString())

                val jsonObjectRequest = object : JsonObjectRequest(Method.POST,url,jsonParams,Response.Listener {

                    if(edtConfirmPassword.text.toString()==edtPassword.text.toString()) {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                Log.e("response", "success" + it)
                                val response = data.getJSONObject("data")
                                sharedPreferences.edit()
                                    .putString("user_id", response.getString("user_id")).apply()
                                sharedPreferences.edit()
                                    .putString("name", response.getString("name")).apply()
                                sharedPreferences.edit()
                                    .putString("email", response.getString("email")).apply()
                                sharedPreferences.edit()
                                    .putString("mobile_number", response.getString("mobile_number"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("address", response.getString("address")).apply()
                                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                val errorMessage = data.getString("errorMessage")
                                Toast.makeText(
                                    this,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        } catch (e: JSONException) {
                            Log.e("response", "error is $it")
                            Toast.makeText(
                                this,
                                "Some unexpected error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }else{
                        Toast.makeText(
                            this,
                            "Passwords did not match",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },Response.ErrorListener {

                    Log.e("Error::::", "/post request fail! Error: ${it.message}")

                }) {
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