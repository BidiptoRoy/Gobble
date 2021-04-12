package com.bidiptoroy.gobble.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.adapter.FavouriteRecyclerAdapter
import com.bidiptoroy.gobble.adapter.HomeRecyclerAdapter
import com.bidiptoroy.gobble.database.ResEntity
import com.bidiptoroy.gobble.database.RestaurantDataBase
import com.bidiptoroy.gobble.model.Hotel


class FavouriteFragment : Fragment() {
    lateinit var recyclerFavourite : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter : FavouriteRecyclerAdapter
    lateinit var progressBar: ProgressBar
    lateinit var progressBarLayout: RelativeLayout
   var dbResList = listOf<ResEntity>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_favourite, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        layoutManager = LinearLayoutManager(activity)
        progressBarLayout = view.findViewById(R.id.favouriteProgressBarLayout)
        progressBar = view.findViewById(R.id.favouriteProgressBar)
        progressBarLayout.visibility = View.VISIBLE
        dbResList = RetrieveFavourites(activity as Context).execute().get()
        if(activity != null){
            progressBarLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context,dbResList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }



        return view
    }
    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<ResEntity>>(){
        override fun doInBackground(vararg params: Void?): List<ResEntity> {
            val db = Room.databaseBuilder(context,RestaurantDataBase :: class.java,"Restaurant-db").build()
            return db.resDao().getAllRestaurants()
        }

    }


}