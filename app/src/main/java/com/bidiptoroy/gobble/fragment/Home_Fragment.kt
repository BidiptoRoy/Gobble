package com.bidiptoroy.gobble.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bidiptoroy.gobble.util.ConnectionManager
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.adapter.HomeRecyclerAdapter
import com.bidiptoroy.gobble.model.Hotel
import com.bidiptoroy.gobble.util.Sorter
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class Home_Fragment : Fragment()
{
    lateinit var recyclerHome : RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var recyclerAdapter : HomeRecyclerAdapter
    lateinit var progressBar: ProgressBar
    lateinit var progressBarLayout:RelativeLayout
    var hotelInfoList = arrayListOf<Hotel>()
    private var checkedItem: Int = -1
    private var filters = arrayListOf("High to Low Cost","Low to High Cost","Ratings")
    var ratingComparator = Comparator<Hotel>{
            hotel1,hotel2 ->
        hotel1.rating.compareTo(hotel2.rating,true)
    }
    var costComparator = Comparator<Hotel>{
            hotel1,hotel2 ->
        hotel1.cost_for_one.compareTo(hotel2.cost_for_one,true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_home_, container, false)
        recyclerHome = view.findViewById(R.id.recyclerHome)
//        layoutManager = LinearLayoutManager(activity)
        setHasOptionsMenu(true)


        recyclerAdapter = HomeRecyclerAdapter(activity as Context,hotelInfoList)
         layoutManager = LinearLayoutManager(activity)
        recyclerHome.layoutManager=layoutManager
        recyclerHome.itemAnimator = DefaultItemAnimator()
        recyclerHome.adapter = recyclerAdapter
        progressBarLayout = view.findViewById(R.id.progressBarLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressBarLayout.visibility = View.VISIBLE
        setUpRecycler(view)

        return view
    }
    private fun setUpRecycler(view: View){
        recyclerHome = view.findViewById(R.id.recyclerHome) as RecyclerView
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"


        if (ConnectionManager().checkConnectivity(activity as Context))
        {
            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
                Response.Listener<JSONObject> {
                    try
                    {
                        progressBarLayout.visibility=View.GONE
                        val data = it.getJSONObject("data")
                        val success : Boolean = data.getBoolean("success")
                        
                        if(success)
                        {
                            Log.e("response","success"+it)
                            val hotelArray = data.getJSONArray("data")
                            for (i in 0 until hotelArray.length())
                            {

                                val resObject = hotelArray.getJSONObject(i)
                                val restaurant = Hotel(
                                    resObject.getString("id"),
                                    resObject.getString("name"),
                                    resObject.getString("rating"),
                                    resObject.getString("cost_for_one"),
                                    resObject.getString("image_url")
                                )
                                hotelInfoList.add(restaurant)
                            }
                            if(activity != null){

                                recyclerAdapter = HomeRecyclerAdapter(activity as Context,hotelInfoList)
                                layoutManager = LinearLayoutManager(activity)
                                recyclerHome.layoutManager=layoutManager
                                recyclerHome.itemAnimator = DefaultItemAnimator()
                                recyclerHome.adapter = recyclerAdapter

                            }
                        }else {
                            Toast.makeText(
                                activity as Context,
                                "Some unexpected error occurred okay",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("response", "error is $it")
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {

                    Log.e("response", "error is $it")
                    if(activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred ",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }) {
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

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.sorting_menu,menu) // setHasOptionsMenu(true) opore likhte hobe // only for fragment
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.action_sort -> showDialog(context as Context)
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showDialog(context: Context){

        val builder: AlertDialog.Builder? = AlertDialog.Builder(context)
        builder?.setTitle("Sort By?")

        val options = arrayOf( "Cost Low to High", "Cost High to Low","Ratings")
        builder?.setSingleChoiceItems(options, checkedItem) { dialog, which ->
            // user checked an item
            checkedItem = which
        }


        builder?.setPositiveButton("Ok") { _, _ ->

            when (checkedItem) {
                0 -> {
                    Collections.sort(hotelInfoList, costComparator)
                }
                1 -> {
                    Collections.sort(hotelInfoList, costComparator)
                    hotelInfoList.reverse()
                }
                2 -> {
                    Collections.sort(hotelInfoList, ratingComparator)
                    hotelInfoList.reverse()
                }
            }
            recyclerAdapter.notifyDataSetChanged()
        }
        builder?.setNegativeButton("Cancel") { _, _ ->

        }
        builder?.create()
        builder?.show()


    }
}