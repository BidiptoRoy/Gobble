package com.bidiptoroy.gobble.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.adapter.HomeRecyclerAdapter
import com.bidiptoroy.gobble.adapter.OrderHistoryRecyclerAdapter
import com.bidiptoroy.gobble.model.HistoryRestaurant
import com.bidiptoroy.gobble.util.ConnectionManager
import org.json.JSONException


class HistoryFragment : Fragment() {

    lateinit var recyclerHistoryRestaurant : RecyclerView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerRestaurantAdapter : OrderHistoryRecyclerAdapter
    lateinit var layourManager : RecyclerView.LayoutManager
    var resList = arrayListOf<HistoryRestaurant>()
    lateinit var progressBar: ProgressBar
    lateinit var progressbarlayout : RelativeLayout





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerHistoryRestaurant=view.findViewById(R.id.historyRes)
        progressBar = view.findViewById(R.id.orderHistoryProgressBar)
        progressbarlayout=view.findViewById(R.id.orderHistoryProgressLayout)
        progressbarlayout.visibility = View.VISIBLE



        layourManager=LinearLayoutManager(activity as Context)
        sharedPreferences = (activity as FragmentActivity)
            .getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)

        var user_id = sharedPreferences.getString("user_id",null)



        recyclerRestaurantAdapter = OrderHistoryRecyclerAdapter(activity as Context,resList)
        recyclerHistoryRestaurant.adapter = recyclerRestaurantAdapter
        recyclerHistoryRestaurant.layoutManager = layourManager
        recyclerHistoryRestaurant.itemAnimator = DefaultItemAnimator()


        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$user_id"
        if(ConnectionManager().checkConnectivity(activity as Context)){

            val jsonObjectRequest = object : JsonObjectRequest(Method.GET,url,null,Response.Listener {

                try{

                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if(success){
                        progressbarlayout.visibility = View.GONE


                        Log.e("response","success"+it)
                        val restaurantArray = data.getJSONArray("data")
                        for(i in 0 until restaurantArray.length())
                        {

                            val resObject = restaurantArray.getJSONObject(i)
                            val itemArray = resObject.getJSONArray("food_items")

                            val restaurant = HistoryRestaurant(
                                resObject.getString("order_id"),
                                resObject.getString("restaurant_name"),
                                resObject.getString("total_cost"),
                                resObject.getString("order_placed_at"),
                                itemArray.length().toString()
                            )
                            resList.add(restaurant)

                            if(activity != null)
                            {
                                recyclerRestaurantAdapter = OrderHistoryRecyclerAdapter(activity as Context,resList)
                                recyclerHistoryRestaurant.adapter = recyclerRestaurantAdapter
                                recyclerHistoryRestaurant.layoutManager = layourManager
                                recyclerHistoryRestaurant.itemAnimator = DefaultItemAnimator()

//                                recyclerHistoryRestaurant.addItemDecoration(
//                                    DividerItemDecoration(
//                                        recyclerHistoryRestaurant.context,
//                                        (layourManager as LinearLayoutManager).orientation
//                                    )
//                                )

                            }

                        }



                    }else {
                        Log.e("response","error"+it)
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred okay",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }catch (e: JSONException) {
                    Log.e("response", "error is $it")
                    Toast.makeText(
                        activity as Context,
                        "Some unexpected error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }



            },Response.ErrorListener {

                Log.e("response", "error is $it")
                if(activity != null) {
                    Toast.makeText(
                        activity as Context,
                        "Volley error occurred ",
                        Toast.LENGTH_LONG
                    ).show()
                }

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
            val dialog =  AlertDialog.Builder(activity as Context)
            dialog.setTitle("Some Problem occurred")
                .setMessage("No Internet Connection ")
                .setPositiveButton("Open Settings"){text, listener ->

                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    activity?.finish()

                }
                .setNegativeButton("Exit"){text, listener ->
                    ActivityCompat.finishAffinity(activity as Activity)
                }
                .create().show()
        }


        return view
    }


}