package com.bidiptoroy.gobble.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.adapter.OrderHistoryRecyclerAdapter
import com.bidiptoroy.gobble.model.HistoryRestaurant
import com.bidiptoroy.gobble.util.ConnectionManager
import org.json.JSONException

class HistoryMenuList : AppCompatActivity() {

    lateinit var recyclerItems : RecyclerView
    lateinit var txtResName : TextView
    lateinit var recyclerAdapter : OrderHistoryRecyclerAdapter.HistoryItemsRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var layoutManager : RecyclerView.LayoutManager
    var itemList = arrayListOf<HistoryRestaurant.HistoryItems>()
    lateinit var progressBar: ProgressBar
    lateinit var progresslayout : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_menu_list)

        progressBar=findViewById(R.id.lastProgressBar)
        progresslayout = findViewById(R.id.lastProgressBarLayout)
        progresslayout.visibility = View.VISIBLE

        recyclerItems=findViewById(R.id.historyItems)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        txtResName=findViewById(R.id.menuItemResName)
        txtResName.text = "Items Ordered from "+intent.getStringExtra("ResName")
        val orderId = intent.getStringExtra("order_id")
        layoutManager = LinearLayoutManager(this)
        recyclerAdapter = OrderHistoryRecyclerAdapter.HistoryItemsRecyclerAdapter(this,itemList)
        recyclerItems.adapter = recyclerAdapter


        var user_id = sharedPreferences.getString("user_id",null)
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$user_id"
        if(ConnectionManager().checkConnectivity(this)){

            val jsonObjectRequest = object : JsonObjectRequest(Method.GET,url,null,

                Response.Listener {

                    try{

                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if(success){
                            progresslayout.visibility = View.GONE


                            Log.e("response","success"+it)
                            val restaurantArray = data.getJSONArray("data")
                            for(i in 0 until restaurantArray.length()){
                                val resObject = restaurantArray.getJSONObject(i)
                                if(orderId==resObject.getString("order_id")) {

                                    val itemArray = resObject.getJSONArray("food_items")
                                    for (j in 0 until itemArray.length()) {
                                        val itemObject = itemArray.getJSONObject(j)
                                        val item = HistoryRestaurant.HistoryItems(
                                            itemObject.getString("food_item_id"),
                                            itemObject.getString("name"),
                                            itemObject.getString("cost")
                                        )
                                        itemList.add(item)

                                    }

                                }

                            }
                            layoutManager = LinearLayoutManager(this)
                            recyclerAdapter = OrderHistoryRecyclerAdapter.HistoryItemsRecyclerAdapter(this,itemList)
                            recyclerItems.adapter = recyclerAdapter
                            recyclerItems.layoutManager = layoutManager



                        }else {
                            Log.e("response","error"+it)
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

                    Log.e("response", "error is $it")

                        Toast.makeText(
                            this,
                            "Volley error occurred ",
                            Toast.LENGTH_LONG
                        ).show()


            }){
                override fun getHeaders(): MutableMap<String, String> {
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