package com.bidiptoroy.gobble.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Menu")
data class MenuEntity(

    @PrimaryKey  var id: String,
     @ColumnInfo(name = "name")   var name : String,
     @ColumnInfo(name = "cost_for_one")  var cost_for_one : String
)
