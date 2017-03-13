package eldmind.cz3002.ntu.eldmind.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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

    public boolean createElderly(int phoneNumber){
        ContentValues values = new ContentValues();
        values.put(EldmindSQLiteHelper.COLUMN_Elderly_PHONENUMBER, phoneNumber);
        try{
            long insertId = database.insert(EldmindSQLiteHelper.TABLE_Elderly, null, values);
            return true;
        }catch(SQLException ex){
            return false;
        }
    }

    public List<Elderly> getAllElderly() {
        List<Elderly> es = new ArrayList<Elderly>();
        Cursor cursor = database.query(EldmindSQLiteHelper.TABLE_Elderly,allColumns,null,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Elderly tr = cursorToElderly(cursor);
            es.add(tr);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return es;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }

    private Elderly cursorToElderly(Cursor cursor) {
        Elderly e = new Elderly(cursor.getInt(0));
        return e;
    }
}
