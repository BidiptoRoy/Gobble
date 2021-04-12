package com.bidiptoroy.gobble.model

data class HistoryRestaurant (var order_id : String,
                              var restaurant_name: String,
                               var total_cost : String ,
                              var order_placed_at : String,
                              var count : String  ){
   data class HistoryItems(var food_item_id: String,var name : String , var cost: String)
}


