package eldmind.cz3002.ntu.eldmind.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by n on 11/2/2017.
 */

public class EldmindSQLiteHelper extends SQLiteOpenHelper {
    public static final String Table_User = "User";
    public static final String COLUMN_User_PHONENUMBER = "_id";
    public static final String COLUMN_User_FIREBASETOKEN = "title";

    public static final String TABLE_TaskReminder = "TaskReminder";
    public static final String COLUMN_TaskReminder_ID = "_id";
    public static final String COLUMN_TaskReminder_TITLE = "title";
    public static final String COLUMN_TaskReminder_DESC = "desc";
    public static final String COLUMN_TaskReminder_RECURRING = "recurring"; //single, daily, weekly
    public static final String COLUMN_TaskReminder_DUETIME = "dueTime";//FOR SINGLE
    public static final String COLUMN_TaskReminder_WEEKLYDAY = "weeklyDay";//FOR WEEKLY EG "MONDAY,TUESDAY,FRIDAY,SUNDAY"
    public static final String COLUMN_TaskReminder_WEEKLYTIME = "weeklyTime";//FOR WEEKLY EG "23:41"
    public static final String COLUMN_TaskReminder_STATUS = "status";//ENABLED OR DISABLED

    public static final String TABLE_Elderly = "Elderly";
    public static final String COLUMN_Elderly_PHONENUMBER = "phoneNumber";

    private static final String DATABASE_NAME = "eldmind.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE_USER = "CREATE TABLE "
            + Table_User + "( " +
            COLUMN_User_PHONENUMBER + " integer default -1, " +
            COLUMN_User_FIREBASETOKEN + " text not null "+
            ");";
    private static final String DATABASE_CREATE_TASKREMINDER = "CREATE TABLE "
            + TABLE_TaskReminder + "( " +
            COLUMN_TaskReminder_ID + " integer primary key autoincrement, " +
            COLUMN_TaskReminder_TITLE + " text not null, "+
            COLUMN_TaskReminder_DESC + " text not null, "+
            COLUMN_TaskReminder_RECURRING + " text not null, "+
            COLUMN_TaskReminder_DUETIME + " integer null, "+
            COLUMN_TaskReminder_WEEKLYDAY+ " text null, "+
            COLUMN_TaskReminder_WEEKLYTIME+ " text null, "+
            COLUMN_TaskReminder_STATUS+ " text not null default 'ENABLED'"+
            ");";
    private static final String DATABASE_CREATE_ELDERLY = "CREATE TABLE "
            + TABLE_Elderly + "( " +
            COLUMN_Elderly_PHONENUMBER + " integer primary key" +
            ");";

    public EldmindSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_USER);
        database.execSQL(DATABASE_CREATE_TASKREMINDER);
        database.execSQL(DATABASE_CREATE_ELDERLY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(EldmindSQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Table_User);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TaskReminder);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Elderly);
        onCreate(db);
    }
}
