package com.bidiptoroy.gobble.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao

interface MenuDao {

    @Insert
    fun insertMenu(menuEntity: MenuEntity)

    @Delete
    fun deleteMenu(menuEntity: MenuEntity)

    @Query(value = "SELECT * FROM Menu")
    fun getAllMenu(): List<MenuEntity>



    @Query(value = "SELECT * FROM Menu WHERE id = :id")
    fun getMenuById(id : String): MenuEntity

    @Query(value ="DELETE FROM Menu WHERE id = :id")
    fun deleteOrders(id : String)
}