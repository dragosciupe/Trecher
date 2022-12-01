package com.example.mvvmmovieapp.di

import android.content.Context
import androidx.room.Room
import com.example.mvvmmovieapp.database.MovieDatabase
import com.example.mvvmmovieapp.util.DataStoreUtil
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
    fun provideContext(@ApplicationContext context: Context) = context

    @Provides
    @Singleton
    fun provideDataStoreUtil(context: Context) =
        DataStoreUtil(context)

    @Provides
    @Singleton
    fun provideMovieDatabase(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            MovieDatabase::class.java,
            "movie_db.DB"
        ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideMoviesDao(db: MovieDatabase) = db.getSavedMoviesDao()
}