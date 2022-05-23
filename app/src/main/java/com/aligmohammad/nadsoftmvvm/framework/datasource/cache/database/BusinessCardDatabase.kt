package com.aligmohammad.nadsoftmvvm.framework.datasource.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aligmohammad.nadsoftmvvm.framework.datasource.cache.model.BusinessCardCacheEntity

@Database(entities = [BusinessCardCacheEntity::class ], version = 1)
abstract class BusinessCardDatabase: RoomDatabase() {

    abstract fun businessCardDao(): BusinessCardDao

    companion object{
        val DATABASE_NAME: String = "businesscard_db"
    }


}