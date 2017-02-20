package eldmind.cz3002.ntu.eldmind.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by n on 11/2/2017.
 */

public class EldmindSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_TaskReminder = "TaskReminder";
    public static final String COLUMN_TaskReminder_ID = "_id";
    public static final String COLUMN_TaskReminder_TITLE = "title";
    public static final String COLUMN_TaskReminder_DESC = "desc";
    public static final String COLUMN_TaskReminder_DUETIME = "dueTime";
    public static final String COLUMN_TaskReminder_RECURRING = "recurring"; //single, daily, weekly, monthly

    private static final String DATABASE_NAME = "eldmind.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_TaskReminder + "( " +
            COLUMN_TaskReminder_ID + " integer primary key autoincrement, " +
            COLUMN_TaskReminder_TITLE + " text not null, "+
            COLUMN_TaskReminder_DESC + " text not null, "+
            COLUMN_TaskReminder_DUETIME + " integer not null, "+
            COLUMN_TaskReminder_RECURRING + " text not null "+
            ");";

    public EldmindSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(EldmindSQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TaskReminder);
        onCreate(db);
    }
}
