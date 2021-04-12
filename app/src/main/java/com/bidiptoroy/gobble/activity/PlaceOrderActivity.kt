package com.bidiptoroy.gobble.activity

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.adapter.PlaceOrderRecyclerAdapter
import com.bidiptoroy.gobble.adapter.ResMenuAdapter
import com.bidiptoroy.gobble.database.MenuDataBase
import com.bidiptoroy.gobble.database.MenuEntity
import com.bidiptoroy.gobble.database.ResEntity
import com.bidiptoroy.gobble.database.RestaurantDataBase
import com.bidiptoroy.gobble.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PlaceOrderActivity : AppCompatActivity() {

    lateinit var recyclerPlaceOrder : RecyclerView
    lateinit var recyclerAdapter: PlaceOrderRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var resName:String
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var txtResName : TextView
    lateinit var ResId:String
    lateinit var btnPlaceOrder : Button
    lateinit var preogressLayout:RelativeLayout
    lateinit var preogressBar: ProgressBar

        var dbMenuList = listOf<MenuEntity>()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)
        toolbar = findViewById(R.id.placeOrderToolBar)
        preogressBar=findViewById(R.id.placeOrderProgressBar)
        preogressLayout = findViewById(R.id.placeOrderProgressLayout)
        preogressLayout.visibility=View.GONE

        recyclerPlaceOrder= findViewById(R.id.recyclerPlaceOrder)
        layoutManager= LinearLayoutManager(this)
        btnPlaceOrder=findViewById(R.id.btnConfirmOder)
        txtResName=findViewById(R.id.placeOrderResName)

        resName = intent.getStringExtra("name")
        ResId = intent.getStringExtra("resID")
        txtResName.text = "Ordering From : "+resName


        dbMenuList = RetrieveFavourites(this).execute().get()
        recyclerAdapter = PlaceOrderRecyclerAdapter(this,dbMenuList)
        recyclerPlaceOrder.adapter = recyclerAdapter
        recyclerPlaceOrder.layoutManager = layoutManager

        var total = 0
        for(i in 0 until dbMenuList.size){

            total += dbMenuList[i].cost_for_one.toInt()
        }



        if(total == 0) {
            btnPlaceOrder.text = "Your cart is empty !"
           btnPlaceOrder.setOnClickListener {   Toast.makeText(this," Add some Items In the Cart",Toast.LENGTH_SHORT).show()
           }
        }
        if(total > 0) {
            btnPlaceOrder.text = "Place Order ( Total Rs."+total.toString()+")"
            btnPlaceOrder.setOnClickListener{


                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/place_order/fetch_result/"

                if(ConnectionManager().checkConnectivity(this)) {


                    val jsonParams = JSONObject()
                    jsonParams.put("user_id",this.getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
                        .getString("user_id",null))
                    jsonParams.put("restaurant_id",ResId)
                    jsonParams.put("total_cost",total.toString())
                    val foodArray = JSONArray()
                    for (i in 0 until dbMenuList.size) {
                        val foodId = JSONObject()
                        foodId.put("food_item_id", dbMenuList[i].id)
                        foodArray.put(i, foodId)
                    }
                    jsonParams.put("food", foodArray)

                    val jsonObjectRequest = object :JsonObjectRequest(Method.POST,url,jsonParams,
                        Response.Listener<JSONObject> {

                            try{

                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if(success){


                                    for (i in 0 until dbMenuList.size) {
                                        ResMenuAdapter.DBAsyncTask(this, dbMenuList[i], 3).execute()
                                    }
                                    startActivity(Intent(this, OrderPlacedActivity::class.java))
                                    finish()

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
    override fun onBackPressed() {

        if(dbMenuList.size>0){

            val dialog =  AlertDialog.Builder(this)
            dialog.setTitle("Alert")
                .setMessage("Your items will be removed form the cart")

                .setPositiveButton("OK"){text, listener ->

                    for (i in 0 until dbMenuList.size){
                        var clearCart = ResMenu.ClearDBAsync(applicationContext, dbMenuList[i].id)
                            .execute().get()
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

    override fun onDestroy() {
        super.onDestroy()
        for (i in 0 until dbMenuList.size){
            var clearCart = ResMenu.ClearDBAsync(applicationContext, dbMenuList[i].id)
                .execute().get()
            finish()
        }

    }




    class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, MenuDataBase::class.java, "Menu-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.menuDao().deleteOrders(resId)
            db.close()
            return true
        }

    }






    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<MenuEntity>>(){
        override fun doInBackground(vararg params: Void?): List<MenuEntity> {
            val db = Room.databaseBuilder(context, MenuDataBase :: class.java,"Menu-db").build()
            return db.menuDao().getAllMenu()
        }

    }
}