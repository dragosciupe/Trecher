package com.example.mvvmmovieapp.di

import android.content.Context
import androidx.room.Room
import com.example.mvvmmovieapp.database.MovieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideMovieDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            MovieDatabase::class.java,
            "movie_db.DB"
        ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideMoviesDao(db: MovieDatabase) = db.getSavedMoviesDao()
}