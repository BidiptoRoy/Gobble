package com.bidiptoroy.gobble.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MenuEntity ::class],version = 1)
abstract class MenuDataBase : RoomDatabase() {
   abstract fun menuDao() : MenuDao
}