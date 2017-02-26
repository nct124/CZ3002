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
 * Created by n on 23/2/2017.
 */

public class ElderlyDataSource {
    // Database fields
    private SQLiteDatabase database;
    private EldmindSQLiteHelper dbHelper;
    private String[] allColumns = { EldmindSQLiteHelper.COLUMN_Elderly_PhoneNumber,EldmindSQLiteHelper.COLUMN_Elderly_FirebaseToken};

    public ElderlyDataSource(Context context) {
        dbHelper = new EldmindSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }

    public boolean createElderly(String firebaseToken){
        ContentValues values = new ContentValues();
        values.put(EldmindSQLiteHelper.COLUMN_Elderly_FirebaseToken, firebaseToken);
        try{
            long insertId = database.insert(EldmindSQLiteHelper.TABLE_Elderly, null, values);
            return true;
        }catch(SQLException ex){
            return false;
        }
    }
    public boolean updateElderly(String newFirebaseToken,String oldFirebaseToken){
        ContentValues args = new ContentValues();
        args.put(EldmindSQLiteHelper.COLUMN_Elderly_FirebaseToken,newFirebaseToken);
        String where = EldmindSQLiteHelper.COLUMN_Elderly_FirebaseToken+"=?";
        String [] whereargs = {oldFirebaseToken};
        return (database.update(EldmindSQLiteHelper.TABLE_Elderly,args,where,whereargs)>0);
    }
    public boolean updateElderly(int phoneNumber,String firebaseToken){
        ContentValues args = new ContentValues();
        args.put(EldmindSQLiteHelper.COLUMN_Elderly_PhoneNumber,phoneNumber);
        String where = EldmindSQLiteHelper.COLUMN_Elderly_FirebaseToken+"=?";
        String [] whereargs = {firebaseToken};
        return (database.update(EldmindSQLiteHelper.TABLE_Elderly,args,where,whereargs)>0);
    }
    public List<Elderly> getAllElderly() {
        List<Elderly> es = new ArrayList<Elderly>();
        Cursor cursor = database.query(EldmindSQLiteHelper.TABLE_Elderly,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Elderly e = cursorToElderly(cursor);
            es.add(e);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return es;
    }
    private Elderly cursorToElderly(Cursor cursor) {
        Elderly e = new Elderly();
        e.setPhone(cursor.getInt(0));
        e.setFirebaseToken(cursor.getString(1));
        return e;
    }
}
