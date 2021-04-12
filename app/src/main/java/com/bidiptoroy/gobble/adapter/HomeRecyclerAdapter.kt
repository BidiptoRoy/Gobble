package com.bidiptoroy.gobble.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.activity.ResMenu
import com.bidiptoroy.gobble.database.ResEntity
import com.bidiptoroy.gobble.database.RestaurantDataBase
import com.bidiptoroy.gobble.model.Hotel
import com.squareup.picasso.Picasso



class HomeRecyclerAdapter(var context:Context,var hotelList: ArrayList<Hotel> )
    : RecyclerView.Adapter<HomeRecyclerAdapter.HomeRecyclerViewHolder>() {
    class  HomeRecyclerViewHolder(view: View):RecyclerView.ViewHolder(view)
    {
        val txtHotelName : TextView = view.findViewById(R.id.hotelName)
        val txtFoodPrice : TextView = view.findViewById(R.id.foodPrice)
        val txtFoodRating : TextView = view.findViewById(R.id.foodRating)
        val imgFoodImage : ImageView = view.findViewById(R.id.foodImage)
        val btnAddToFav : ImageView = view.findViewById(R.id.btnAddToFav)
        val llConntent : LinearLayout = view.findViewById(R.id.homeContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return HomeRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
      return hotelList.size
    }

    override fun onBindViewHolder(holder: HomeRecyclerViewHolder, position: Int)
    {

        val hotel =hotelList[position]
        holder.txtHotelName.text = hotel.name
        holder.txtFoodRating.text = hotel.rating
        holder.txtFoodPrice.text = "Rs."+hotel.cost_for_one+"/person"
        Picasso.get().load(hotel.image_url).error(R.drawable.foodimage).into(holder.imgFoodImage)

        val resEntity = ResEntity(hotel.id,hotel.name,hotel.rating,hotel.cost_for_one,hotel.cost_for_one)
        val checkFav = DBAsyncTask(context,resEntity,1).execute()
        var isFav = checkFav.get()


            if(isFav){

                val favColor = ContextCompat.getColor(context,R.color.colorAccent)
                holder.btnAddToFav.setColorFilter(favColor)
            }else
            {

                    val notfavColor = ContextCompat.getColor(context,R.color.notFav)
                    holder.btnAddToFav.setColorFilter(notfavColor)
            }
        holder.btnAddToFav.setOnClickListener {
            if(!DBAsyncTask(context,resEntity,1).execute().get()){

                val async = DBAsyncTask(context,resEntity,2).execute()
                val result= async.get()
                if(result){

                    holder.btnAddToFav.animate().apply {
                        duration = 1000
                        rotationYBy(360f)
                    }.start()

                    Toast.makeText(context,"Restaurant Added to Favourites", Toast.LENGTH_SHORT).show()

                    val favColor = ContextCompat.getColor(context,R.color.colorAccent)
                    holder.btnAddToFav.setColorFilter(favColor)

                }else{
                    Toast.makeText(context,"Some Error Occurred ", Toast.LENGTH_LONG).show()

                }

            }else{
                val async = DBAsyncTask(context,resEntity,3).execute()
                val result= async.get()
                if(result){
                    Toast.makeText(context,"Restaurant Removed From Favourites", Toast.LENGTH_SHORT).show()
                    val notfavColor = ContextCompat.getColor(context,R.color.notFav)
                    holder.btnAddToFav.setColorFilter(notfavColor)

                }else{
                    Toast.makeText(context,"Some Error Occurred ", Toast.LENGTH_LONG).show()

                }

            }
        }

        holder.llConntent.setOnClickListener {
            val intent = Intent(context,ResMenu :: class.java)
            intent.putExtra("id",hotel.id)
            intent.putExtra("name",hotel.name)
            context.startActivity(intent)
        }

    }

    class DBAsyncTask(val context: Context,val resEntity: ResEntity,val mode : Int): AsyncTask<Void, Void, Boolean>() {


        val db = Room.databaseBuilder(context,RestaurantDataBase::class.java,"Restaurant-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode){
                1 ->{
                    //check db if the Restaurant is favourite or not?

                    val res : ResEntity? = db.resDao().getResById(resEntity.id)
                    db.close()
                    return res != null
                }
                2 ->{
                    //save the Restaurant into db as favourites
                    db.resDao().insertRes(resEntity)

                    return true

                }
                3 ->{
                    //Remove the favourite Restaurant
                    db.resDao().deleteRes(resEntity)
                    return true

                }
            }


            return false
        }

    }
}