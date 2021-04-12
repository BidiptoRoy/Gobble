package com.bidiptoroy.gobble.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResDao {

    @Insert
    fun insertRes(resEntity: ResEntity)

    @Delete
    fun deleteRes(resEntity: ResEntity)

    @Query(value = "SELECT * FROM  Restaurants")
    fun getAllRestaurants() : List<ResEntity>

    @Query(value = "SELECT * FROM Restaurants WHERE id = :res_id")
    fun getResById(res_id: String) : ResEntity


}