package com.bidiptoroy.gobble.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.adapter.HomeRecyclerAdapter
import com.bidiptoroy.gobble.adapter.ResMenuAdapter
import com.bidiptoroy.gobble.database.MenuDataBase
import com.bidiptoroy.gobble.model.Hotel
import com.bidiptoroy.gobble.model.MenuInfo
import com.bidiptoroy.gobble.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

class ResMenu : AppCompatActivity()
{
    lateinit var recyclerResMenu : RecyclerView
    lateinit var menuLayoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter : ResMenuAdapter
    lateinit var progressBar: ProgressBar
    lateinit var progressBarLayout: RelativeLayout
    lateinit var resId:String
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var btnProceedToCart : Button

   var menuInfoList = arrayListOf<MenuInfo>()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_res_menu)
         resId = intent.getStringExtra("id")
        var resName = intent.getStringExtra("name")
        toolbar = findViewById(R.id.menuToolBar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = resName
        btnProceedToCart=findViewById(R.id.btnProceedToCart)
        recyclerResMenu = findViewById(R.id.recyclerResMenu)
        menuLayoutManager = LinearLayoutManager(this)

        progressBar = findViewById(R.id.menuProgressBar)
        progressBarLayout = findViewById(R.id.menuProgressBarLayout)
        progressBarLayout.visibility = View.VISIBLE


        val queue = Volley.newRequestQueue(this)
        var url = "http://13.235.250.119/v2/restaurants/fetch_result/$resId"

        if(ConnectionManager().checkConnectivity(this))
        {

            val jsonObjectRequest = object : JsonObjectRequest(Method.GET,url,null,
                Response.Listener<JSONObject> {

                    try {
                        progressBarLayout.visibility=View.GONE
                        val data = it.getJSONObject("data")
                        val success : Boolean = data.getBoolean("success")
                        if(success)
                        {
                            Log.e("response","success"+it)
                            val itemArray = data.getJSONArray("data")
                            for(i in 0 until itemArray.length()){
                                val itemObject = itemArray.getJSONObject(i)
                                val item = MenuInfo(

                                    itemObject.getString("id"),
                                    itemObject.getString("name"),
                                    itemObject.getString("cost_for_one")
                                )
                                menuInfoList.add(item)

                            }
                           recyclerAdapter= ResMenuAdapter(this,menuInfoList)
                            recyclerResMenu.adapter =recyclerAdapter
                            recyclerResMenu.itemAnimator = DefaultItemAnimator()

                            recyclerResMenu.layoutManager = menuLayoutManager

                        }else {
                            Toast.makeText(
                                this,
                                "Some unexpected error occurred okay",
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

                Toast.makeText(
                    this,
                    "Volley error occurred ",
                    Toast.LENGTH_LONG
                ).show()

            })
            {
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
        btnProceedToCart.setOnClickListener {
            val intent =Intent(this,PlaceOrderActivity::class.java)
            intent.putExtra("name",resName)
            intent.putExtra("resID",resId)
            startActivity(intent)
        }

    }

    override fun onPause() {
        super.onPause()

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        for (i in 0 until menuInfoList.size){
            var clearCart = ClearDBAsync(applicationContext, menuInfoList[i].id).execute().get()
            finish()
        }
    }

    override fun onBackPressed() {

        if(menuInfoList.size>0){

        val dialog =  AlertDialog.Builder(this)
        dialog.setTitle("Alert")
            .setMessage("Your items will be removed form the cart")

            .setPositiveButton("OK"){text, listener ->

                for (i in 0 until menuInfoList.size){
                var clearCart = ClearDBAsync(applicationContext, menuInfoList[i].id).execute().get()
                finish()
                }

            }
            .setNegativeButton("CANCEl"){text, listener ->
                dialog.setCancelable(true)
            }
            .create().show()
        }else{
            super.onBackPressed()
        }
    }
//    /*Asynctask class for clearing the recently added items from the database*/
    class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, MenuDataBase::class.java, "Menu-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.menuDao().deleteOrders(resId)
            db.close()
            return true
        }

    }
//
//    /*When the user presses back, we clear the cart so that when the returns to the cart, there is no
//    * redundancy in the entries*/
//    override fun onSupportNavigateUp(): Boolean
//    {
//        val clearCart =
//            ClearDBAsync(applicationContext, resId.toString()).execute().get()
//        RestaurantMenuAdapter.isCartEmpty = true
//        onBackPressed()
//        return true
//    }

}