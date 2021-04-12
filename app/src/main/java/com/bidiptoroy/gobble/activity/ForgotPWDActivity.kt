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
import org.json.JSONException
import org.json.JSONObject

class ForgotPWDActivity : AppCompatActivity()
{
    lateinit var btnNext: Button
    lateinit var edtMobileNumber: EditText
    lateinit var edtEmail : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_p_w_d)
        btnNext =findViewById(R.id.buttonNext)
        edtMobileNumber=findViewById(R.id.forgotMobileNumber)
        edtEmail = findViewById(R.id.forgotEmail)

        btnNext.setOnClickListener {
           if(ConnectionManager().checkConnectivity(this)){

                val queue = Volley.newRequestQueue(this)
               val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
               val jsonParams = JSONObject()
               jsonParams.put("mobile_number",edtMobileNumber.text.toString())
               jsonParams.put("email",edtEmail.text.toString())

               val jsonObjectRequest = object : JsonObjectRequest(Method.POST,url,jsonParams,Response.Listener {

                    try{

                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if(success){
                            val intent = Intent(this,NewPasswordActivity::class.java)
                            intent.putExtra("mobile_number",edtMobileNumber.text.toString())
                            startActivity(intent)

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