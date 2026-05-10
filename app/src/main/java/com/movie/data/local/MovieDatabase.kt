package com.movie.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteMovie::class, User::class],
    version = 2
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movies.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}