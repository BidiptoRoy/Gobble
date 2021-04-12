package com.bidiptoroy.gobble.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.fragment.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity()
{
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolBar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem? =null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var showName : TextView
    lateinit var showNumber : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolBar=findViewById(R.id.toolBar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView=findViewById(R.id.navigationView)




        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE )


        setUpToolBar()
        openHome()
        val actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,
            R.string.open_Drawer,
            R.string.close_Drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()





        navigationView.setNavigationItemSelectedListener {

            if(previousMenuItem != null){
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem=it

            when(it.itemId)
            {
                R.id.appHome ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        Home_Fragment()
                    )

                        .commit()
                    supportActionBar?.title="HOME"
                    drawerLayout.closeDrawers()
                }
                R.id.favourites ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        FavouriteFragment()
                    )


                        .commit()
                    supportActionBar?.title="FAVOURITE RESTAURANTS"

                    drawerLayout.closeDrawers()
                }
                R.id.profile ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        ProfileFragment()
                    )

                        .commit()
                    supportActionBar?.title="PROFILE"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        FAQSFragment()
                    )

                        .commit()
                    supportActionBar?.title="FAQs"
                    drawerLayout.closeDrawers()
                }
                R.id.history ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        HistoryFragment()
                    )

                        .commit()
                    supportActionBar?.title="Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.logout ->{
//                    supportFragmentManager.beginTransaction().replace(
//                        R.id.frameLayout,
//                        LogOutFragment()
//                    )
//
//                        .commit()
//                    supportActionBar?.title="Log Out"
//                    drawerLayout.closeDrawers()
                 val dialog =   AlertDialog.Builder(this)
                    dialog.setTitle("Are you sure you want to log out?")
                        .setIcon(R.drawable.ic_baseline_add_alert_24)
                        .setPositiveButton("LOG OUT"){text, listener ->

                            sharedPreferences.edit().clear().apply()
                            val intent = Intent(this,SignInActivity::class.java)
                            startActivity(intent)
                            ActivityCompat.finishAffinity(this)

                        }
                        .setNegativeButton("Cancel"){text,listener ->
                            dialog.setCancelable(true)

                        }
                        .create().show()

                }
            }
            return@setNavigationItemSelectedListener true
        }

    }
    fun setUpToolBar()
    {
        setSupportActionBar(toolBar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onBackPressed()
    {
//        super.onBackPressed()
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when(frag){
            !is Home_Fragment -> openHome()
            else -> finish()
        }
    }
    fun openHome()
    {
        val fragment = Home_Fragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout,fragment)
        transaction.commit()
        supportActionBar?.title="HOME"
        navigationView.setCheckedItem(R.id.appHome)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId
        if(id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

}