package com.example.ollegeapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Student::class, Teacher::class, Speciality::class], version = 2)
abstract class CollegeDatabase : RoomDatabase(){

    abstract fun databaseDao():DatabaseDao

    companion object {
        @Volatile
        public var INSTANCE: CollegeDatabase? = null

        fun getDatabase(context: Context): CollegeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CollegeDatabase::class.java,
                    "college_database4"
                ).allowMainThreadQueries()
                    .build()

                INSTANCE = instance
                instance
            }
        }


    }

}