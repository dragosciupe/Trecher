package com.example.mvvmmovieapp.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromList(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(stringList: String): List<Int> {
        return stringList.split(",").map {
            it.toInt()
        }
    }
}