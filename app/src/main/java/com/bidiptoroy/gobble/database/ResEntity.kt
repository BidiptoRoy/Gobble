package com.bidiptoroy.gobble.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Restaurants")
data class ResEntity (
    @PrimaryKey var id:String,
    @ColumnInfo(name = "name")  var name : String,
    @ColumnInfo(name ="rating")  var rating: String,
    @ColumnInfo(name = "cost_for_one") var cost_for_one : String,
    @ColumnInfo(name = "image_url")  var  image_url : String

)
