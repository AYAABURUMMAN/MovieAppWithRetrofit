package com.movie.data.local
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.movie.data.models.MovieDetail

class MovieDatabaseHelper(context: Context)
    : SQLiteOpenHelper(
    context,
    "movies.db",
    null,
    2
) {

    companion object {
        const val TABLE_NAME    = "favorite_movies"
        const val COL_ID        = "id"
        const val COL_TITLE     = "title"
        const val COL_OVERVIEW  = "overview"
        const val COL_POSTER    = "poster_path"
        const val COL_RATING    = "vote_average"
        const val COL_DATE      = "release_date"
    }
    // ① حفظ فيلم
    fun insertMovie(movie: MovieDetail) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COL_ID,       movie.id)
            put(COL_TITLE,    movie.title)
            put(COL_OVERVIEW, movie.overview)
            put(COL_POSTER,   movie.posterPath)
            put(COL_RATING,   movie.voteAverage)
            put(COL_DATE,     movie.releaseDate)
        }

        db.insertOrThrow(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllMovies(): List<MovieDetail> {
        val db = readableDatabase
        val movies = mutableListOf<MovieDetail>()

        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)


        while (cursor.moveToNext()) {
            val movie = MovieDetail(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                overview = cursor.getString(cursor.getColumnIndexOrThrow(COL_OVERVIEW)),
                posterPath = cursor.getString(cursor.getColumnIndexOrThrow(COL_POSTER)),
                backdropPath = null,
                voteAverage = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RATING)),
                releaseDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE))
            )
            movies.add(movie)
        }

        cursor.close()
        db.close()
        return movies
    }


    fun isFavorite(movieId: Int): Boolean {
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COL_ID = ?",
            arrayOf(movieId.toString())
        )

        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun deleteMovie(movieId: Int) {
        val db = writableDatabase

        db.delete(
            TABLE_NAME,
            "$COL_ID = ?",
            arrayOf(movieId.toString())
        )

        db.close()
    }


    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID       INTEGER PRIMARY KEY,
                $COL_TITLE    TEXT,
                $COL_OVERVIEW TEXT,
                $COL_POSTER   TEXT,
                $COL_RATING   REAL,
                $COL_DATE     TEXT
            )
        """.trimIndent()

        db.execSQL(createTable)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}


