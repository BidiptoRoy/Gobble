package com.bidiptoroy.gobble.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ResEntity::class],version = 1)
abstract class RestaurantDataBase : RoomDatabase() {
    abstract fun resDao(): ResDao
}