package com.example.mvvmmovieapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mvvmmovieapp.apidata.trending.MovieItem

@Database(
    entities = [MovieItem::class],
    version = 4
)
@TypeConverters(Converters::class)
abstract class MovieDatabase: RoomDatabase() {

    abstract fun getSavedMoviesDao(): SavedMoviesDao
}