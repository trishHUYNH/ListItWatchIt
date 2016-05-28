package thuynh90.tacoma.uw.edu.listitwatchit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import thuynh90.tacoma.uw.edu.listitwatchit.tabs.Movie;

/**
 * Database to store a user's To Watch list to the device
 */
public class WatchlistDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Watchlist.db";
    private static final String WATCHLIST_TABLE = "Watchlist";

    private WatchlistDBHelper mWatchlistDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public WatchlistDB(Context context) {
        mWatchlistDBHelper = new WatchlistDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mWatchlistDBHelper.getWritableDatabase();
    }

    class WatchlistDBHelper extends SQLiteOpenHelper {

        private static final String CREATE_WATCHLIST_SQL =
                "CREATE TABLE IF NOT EXISTS Watchlist "
                        + "(title TEXT, id TEXT PRIMARY KEY)";

        private static final String DROP_WATCHLIST_SQL =
                "DROP TABLE IF EXISTS Watchlist";

        public WatchlistDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_WATCHLIST_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_WATCHLIST_SQL);
            onCreate(sqLiteDatabase);
        }
    }

    /**
     * Inserts a movie into the local sqlite table. Returns true if successful, false otherwise.
     *
     * @param title Title of the movie
     * @param id TMDB id of the movie
     * @return true or false
     */
    public boolean insertMovie(String title, String id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("id", id);

        long rowId = mSQLiteDatabase.insert("Watchlist", null, contentValues);
        return rowId != -1;
    }

    /**
     * Delete all the data from the WATCHLIST_TABLE
     */
    public void deleteMovies() {
        mSQLiteDatabase.delete(WATCHLIST_TABLE, null, null);
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }

    /**
     * Returns the list of movies from the local Watchlist table.
     * @return list List of movies that were stored to the device
     */
    public List<Movie> getMovies() {

        String[] columns = {
                "title", "id"
        };

        Cursor c = mSQLiteDatabase.query(
                WATCHLIST_TABLE,  // The table to query
                columns,          // The columns to return
                null,             // The columns for the WHERE clause
                null,             // The values for the WHERE clause
                null,             // don't group the rows
                null,             // don't filter by row groups
                null              // The sort order
        );
        c.moveToFirst();
        List<Movie> list = new ArrayList<>();
        for (int i=0; i<c.getCount(); i++) {
            String title = c.getString(0);
            String id = c.getString(1);
            Movie movie = new Movie(title, id);
            list.add(movie);
            c.moveToNext();
        }
        c.close();
        return list;
    }
}
