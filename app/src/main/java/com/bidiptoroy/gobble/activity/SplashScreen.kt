package com.bidiptoroy.gobble.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.bidiptoroy.gobble.R

class SplashScreen : AppCompatActivity()
{
    lateinit var handler:Handler
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        handler = Handler()
        handler.postDelayed({
          var intent =  Intent(this,SignInActivity ::class.java)
            startActivity(intent)
            finish()
        },2000)
        
    }
}