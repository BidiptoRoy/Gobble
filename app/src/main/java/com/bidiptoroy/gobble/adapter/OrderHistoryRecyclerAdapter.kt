package com.bidiptoroy.gobble.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bidiptoroy.gobble.R
import com.bidiptoroy.gobble.activity.HistoryMenuList
import com.bidiptoroy.gobble.model.HistoryRestaurant

class OrderHistoryRecyclerAdapter(var context:Context, var resList: ArrayList<HistoryRestaurant> ): RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {



    class HistoryItemsRecyclerAdapter(var context: Context, var itemList : ArrayList<HistoryRestaurant.HistoryItems>): RecyclerView.Adapter<HistoryItemsRecyclerAdapter.HistoryItemsViewHolder>() {

        class HistoryItemsViewHolder(view : View): RecyclerView.ViewHolder(view) {

            val txtItemName : TextView = view.findViewById(R.id.historyItemName)
            val txtItemCost : TextView = view.findViewById(R.id.historyItemCost)
            val itemContent : LinearLayout = view.findViewById(R.id.historyItemContent)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemsViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_single_row,parent,false)
            return HistoryItemsViewHolder(view)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onBindViewHolder(holder: HistoryItemsViewHolder, position: Int) {

            val item = itemList[position]
            holder.txtItemName.text = item.name
            holder.txtItemCost.text = "Rs. "+item.cost


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_res_single_row,parent,false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return resList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val res = resList[position]
        holder.txtResName.text = res.restaurant_name
        holder.txtOrderDate.text = res.order_placed_at
        holder.txtTotalCost.text = "Total Cost Rs. +"+res.total_cost
        holder.txtItemCount.text = res.count+" Items Ordered"
        holder.resContent.setOnClickListener {
            val intent = Intent(context,HistoryMenuList::class.java)
            intent.putExtra("ResName",res.restaurant_name)
            intent.putExtra("order_id",res.order_id)
            context.startActivity(intent)
        }

//        holder.layoutManager = LinearLayoutManager(context)
//        holder.recyclerAdapter = HistoryItemsRecyclerAdapter(context,resList[position].food_items)
//        holder.recyclerView.adapter = holder.recyclerAdapter
//        holder.recyclerView.layoutManager = holder.layoutManager
    }
    class OrderHistoryViewHolder(view : View): RecyclerView.ViewHolder(view){

        val txtResName : TextView = view.findViewById(R.id.historyResName)
        val txtOrderDate : TextView = view.findViewById(R.id.historyOrderDate)
        val txtItemCount : TextView = view.findViewById(R.id.itemCount)
        val txtTotalCost : TextView = view.findViewById(R.id.orderCost)
        val resContent : LinearLayout = view.findViewById(R.id.historyResContent)

//        val recyclerView : RecyclerView =  view.findViewById(R.id.historyItems)
//
//        lateinit var layoutManager : RecyclerView.LayoutManager
//        lateinit var recyclerAdapter : HistoryItemsRecyclerAdapter
    }
}