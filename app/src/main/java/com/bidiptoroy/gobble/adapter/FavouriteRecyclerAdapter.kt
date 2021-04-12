package com.bidiptoroy.gobble.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.activity.ResMenu
import com.bidiptoroy.gobble.database.ResEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(var context:Context,var resList:List<ResEntity>): RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(view : View):RecyclerView.ViewHolder(view){

        val txtHotelName : TextView = view.findViewById(R.id.favResName)
        val txtFoodPrice : TextView = view.findViewById(R.id.favFoodPrice)
        val txtFoodRating : TextView = view.findViewById(R.id.favFoodRating)
        val imgFoodImage : ImageView = view.findViewById(R.id.favFoodImage)
        val btnAddToFav : ImageView = view.findViewById(R.id.favBtnAddToFav)
        val llConntent : LinearLayout = view.findViewById(R.id.favContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_favourite_single_row,parent,false)
        return FavouriteViewHolder(view)

    }

    override fun getItemCount(): Int {
        return  resList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int)
    {
        val res = resList[position]
        holder.txtHotelName.text = res.name
        holder.txtFoodRating.text = res.rating
        Picasso.get().load(res.image_url).error(R.drawable.foodimage).into(holder.imgFoodImage)
        holder.txtFoodPrice.text = "Rs. "+res.cost_for_one+" /person"

        val checkFav = HomeRecyclerAdapter.DBAsyncTask(context,res,1).execute()
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
            if(!HomeRecyclerAdapter.DBAsyncTask(context,res,1).execute().get()){
                val async = HomeRecyclerAdapter.DBAsyncTask(context,res,2).execute()
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

                val async = HomeRecyclerAdapter.DBAsyncTask(context,res,3).execute()
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
            val intent = Intent(context, ResMenu :: class.java)
            intent.putExtra("id",res.id)
            intent.putExtra("name",res.name)
            context.startActivity(intent)
        }



    }
}