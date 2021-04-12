package com.bidiptoroy.gobble.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.database.MenuEntity

class PlaceOrderRecyclerAdapter(var context: Context,var itemList:List<MenuEntity>): RecyclerView.Adapter<PlaceOrderRecyclerAdapter.PlaceOrderViewHolder>() {

    class PlaceOrderViewHolder(view : View):RecyclerView.ViewHolder(view){

        val txtItemCount :TextView= view.findViewById(R.id.placeOrderItemCount)
        val txtItemName : TextView = view.findViewById(R.id.placeOrderItemName)
        val txtItemPrice : TextView = view.findViewById(R.id.placeOrderItemPrice)
        val llContent : LinearLayout = view.findViewById(R.id.placeOrderContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reclycler_place_order_single_row,parent,false)
        return PlaceOrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }



    override fun onBindViewHolder(holder: PlaceOrderViewHolder, position: Int) {

        val item = itemList[position]
        holder.txtItemCount.text = (position+1).toString()
        holder.txtItemName.text = item.name
        holder.txtItemPrice.text = item.cost_for_one


    }
}