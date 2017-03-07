package eldmind.cz3002.ntu.eldmind.SQL;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import eldmind.cz3002.ntu.eldmind.model.Elderly;

/**
 * Created by n on 7/3/2017.
 */

public class ElderlyDataSource {
    private SQLiteDatabase database;
    private EldmindSQLiteHelper dbHelper;
    private String[] allColumns = { EldmindSQLiteHelper.COLUMN_Elderly_PHONENUMBER};

    private String TAG = "ElderlyDataSource";

    public ElderlyDataSource(Context context) {
        dbHelper = new EldmindSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }

    private Elderly cursorToTaskReminder(Cursor cursor) {
        Elderly e = new Elderly(cursor.getInt(0));
        return e;
    }
}
