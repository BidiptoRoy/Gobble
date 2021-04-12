package com.bidiptoroy.gobble.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.database.MenuDataBase
import com.bidiptoroy.gobble.database.MenuEntity
import com.bidiptoroy.gobble.database.ResEntity
import com.bidiptoroy.gobble.model.MenuInfo


class ResMenuAdapter(var context:Context,var menuList: ArrayList<MenuInfo> ): RecyclerView.Adapter<ResMenuAdapter.ResMenuViewHolder>() {

    class ResMenuViewHolder(view: View):RecyclerView.ViewHolder(view)
    {
        val txtItemName : TextView = view.findViewById(R.id.itemName)
        val txtItemCount : TextView = view.findViewById(R.id.menuCount)
        val menuContent : LinearLayout = view.findViewById(R.id.resMenuContent)
        val txtItemCost : TextView = view.findViewById(R.id.itemCost)
        val btnAddToCart : Button = view.findViewById(R.id.addToCart)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_resmenu_single_row,parent,false)
        return ResMenuViewHolder(view)
    }

    override fun getItemCount(): Int {
       return menuList.size
    }

    override fun onBindViewHolder(holder: ResMenuViewHolder, position: Int)
    {
        val item = menuList[position]
        holder.txtItemCount.text = (position+1).toString()
        holder.txtItemName.text = item.name
        holder.txtItemCost.text = "Rs. "+item.cost_for_one

        val menuEntity = MenuEntity(item.id,item.name,item.cost_for_one)

        val checkFav = DBAsyncTask(context,menuEntity,1).execute()
        var isAdded = checkFav.get()

            if(isAdded){

                val favColor = ContextCompat.getColor(context,R.color.colorAccent)
                holder.btnAddToCart.setBackgroundColor(favColor)
                holder.btnAddToCart.text = "REMOVE"
            }else
            {

                val notAddedColor = ContextCompat.getColor(context,R.color.addToCart)
                holder.btnAddToCart.setBackgroundColor(notAddedColor)
                holder.btnAddToCart.text = "ADD"
            }
         holder.btnAddToCart.setOnClickListener {
//             !DBAsyncTask(applicationContext,bookEntity,1).execute().get()
           if(!DBAsyncTask(context,menuEntity,1).execute().get())
           {
               val async = DBAsyncTask(context,menuEntity,2).execute()
               val result = async.get()
               if(result){
                   Toast.makeText(context,"Item Added to Cart", Toast.LENGTH_SHORT).show()
                   val favColor = ContextCompat.getColor(context,R.color.colorAccent)
                   holder.btnAddToCart.setBackgroundColor(favColor)
                   holder.btnAddToCart.text = "REMOVE"

               }else
               {
                 Toast.makeText(context,"Some Error Occurred ", Toast.LENGTH_LONG).show()

               }
            }else{
               val async = DBAsyncTask(context,menuEntity,3).execute()
               val result = async.get()
               if(result){
                   Toast.makeText(context,"Item Removed from Cart", Toast.LENGTH_SHORT).show()
                   val notAddedColor = ContextCompat.getColor(context,R.color.addToCart)
                   holder.btnAddToCart.setBackgroundColor(notAddedColor)
                   holder.btnAddToCart.text = "ADD"

               }
           }
         }



    }
    class DBAsyncTask(val context: Context,val menuEntity: MenuEntity,val mode: Int): AsyncTask<Void,Void,Boolean>(){

        val db = Room.databaseBuilder(context,MenuDataBase ::class.java,"Menu-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode){
                1 ->{
                    val menu : MenuEntity? = db.menuDao().getMenuById(menuEntity.id)
                    db.close()
                    return menu != null
                }
                2 ->{
                    db.menuDao().insertMenu(menuEntity)

                    return true
                }
                3 ->{
                    db.menuDao().deleteMenu(menuEntity)
                    return true
                }
            }

            return false

        }

    }
}