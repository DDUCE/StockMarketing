package DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {



    // Table Name
    public static final String TABLE_NAME = "WATCHLIST";

    // Table columns
    public static final String _ID = "id";
    public static final String company_short_code = "company_short_code";
    public static final String company_full_name = "company_full_name";
    public static final String company_code = "company_code";



    // Database Information
    static final String DB_NAME = "DBWATCHLIST.sqlite";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + " (" +
            _ID +  " INTEGER PRIMARY KEY," +
            company_short_code + " TEXT," +
            company_full_name + " TEXT,"+
            company_code + " TEXT"+");";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}