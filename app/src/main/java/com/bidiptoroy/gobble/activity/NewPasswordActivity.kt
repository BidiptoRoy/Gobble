package com.bidiptoroy.gobble.activity

import android.app.AlertDialog
import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_new_password.*
import org.json.JSONException
import org.json.JSONObject

class NewPasswordActivity : AppCompatActivity()
{
    lateinit var btnSubmit : Button
    lateinit var edtOTP : EditText
    lateinit var edtNewPWD : EditText
    lateinit var edtconfirmPWD : EditText
    lateinit var mobileNumber : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        btnSubmit = findViewById(R.id.buttonSubmit)
        edtOTP = findViewById(R.id.edtOTP)
        edtNewPWD =findViewById(R.id.edtNewPWD)
        edtconfirmPWD = findViewById(R.id.edtConfirmNewPWD)

        mobileNumber = intent.getStringExtra("mobile_number")


//            val intent = Intent(this,SignInActivity ::class.java)
//            startActivity(intent)

        btnSubmit.setOnClickListener {

            if(ConnectionManager().checkConnectivity(this)){

                if(edtNewPWD.text.toString() == edtconfirmPWD.text.toString()){

                    val queue = Volley.newRequestQueue(this)
                   val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number",mobileNumber)
                    jsonParams.put("password",edtNewPWD.text.toString())
                    jsonParams.put("otp",edtOTP.text.toString())

                    val jsonObjectRequest = object : JsonObjectRequest(Method.POST,url,jsonParams,Response.Listener {

                    try{

                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        val successMessage = data.getString("successMessage")
                        if(success){
                            Toast.makeText(this,successMessage,Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,SignInActivity::class.java))
                        }else{
                            Toast.makeText(this,"TRY AGAIN",Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show()
                }

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