package com.example.pizzoompas.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pizzeria::class], version = 1, exportSchema = false)
abstract class PizzeriaDatabase : RoomDatabase() {
    abstract fun pizzeriaDao(): PizzeriaDao
    companion object {
        @Volatile
        private var INSTANCE: PizzeriaDatabase? = null
        fun getDatabase(context: Context): PizzeriaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PizzeriaDatabase::class.java,
                    "pizzeria_database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }
}