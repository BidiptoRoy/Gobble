package com.bidiptoroy.gobble.util

import com.bidiptoroy.gobble.model.Hotel

class Sorter {

    var ratingComparator = Comparator<Hotel>{
            hotel1,hotel2 ->
        hotel1.rating.compareTo(hotel2.rating,true)
    }
    var costComparator = Comparator<Hotel>{
            hotel1,hotel2 ->
        hotel1.cost_for_one.compareTo(hotel2.cost_for_one,true)
    }

}