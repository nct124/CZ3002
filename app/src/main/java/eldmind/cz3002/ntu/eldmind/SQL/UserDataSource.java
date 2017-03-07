package eldmind.cz3002.ntu.eldmind.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eldmind.cz3002.ntu.eldmind.model.User;

/**
 * Created by n on 23/2/2017.
 */

public class UserDataSource {
    // Database fields
    private SQLiteDatabase database;
    private EldmindSQLiteHelper dbHelper;
    private String[] allColumns = { EldmindSQLiteHelper.COLUMN_User_PHONENUMBER,EldmindSQLiteHelper.COLUMN_User_FIREBASETOKEN};

    public UserDataSource(Context context) {
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
        values.put(EldmindSQLiteHelper.COLUMN_User_FIREBASETOKEN, firebaseToken);
        try{
            long insertId = database.insert(EldmindSQLiteHelper.Table_User, null, values);
            return true;
        }catch(SQLException ex){
            return false;
        }
    }

    public boolean removePhoneNumber() {
        ContentValues cv = new ContentValues();
        cv.put(EldmindSQLiteHelper.COLUMN_User_PHONENUMBER, -1);
        try {
            database.update(EldmindSQLiteHelper.Table_User,
                    cv, null, null);
        } catch (SQLException ex) {
            Log.d("UserDataSource", "Error===>" + ex);
        }
        return true;
    }

    public boolean updateElderly(String newFirebaseToken,String oldFirebaseToken){
        ContentValues args = new ContentValues();
        args.put(EldmindSQLiteHelper.COLUMN_User_FIREBASETOKEN,newFirebaseToken);
        String where = EldmindSQLiteHelper.COLUMN_User_FIREBASETOKEN+"=?";
        String [] whereargs = {oldFirebaseToken};
        return (database.update(EldmindSQLiteHelper.Table_User,args,where,whereargs)>0);
    }
    public boolean updateElderly(int phoneNumber,String firebaseToken){
        ContentValues args = new ContentValues();
        args.put(EldmindSQLiteHelper.COLUMN_User_PHONENUMBER,phoneNumber);
        String where = EldmindSQLiteHelper.COLUMN_User_FIREBASETOKEN+"=?";
        String [] whereargs = {firebaseToken};
        return (database.update(EldmindSQLiteHelper.Table_User,args,where,whereargs)>0);
    }
    public List<User> getAllUser() {
        List<User> es = new ArrayList<User>();
        Cursor cursor = database.query(EldmindSQLiteHelper.Table_User,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User e = cursorToUser(cursor);
            es.add(e);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return es;
    }
    private User cursorToUser(Cursor cursor) {
        User e = new User();
        e.setPhone(cursor.getInt(0));
        e.setFirebaseToken(cursor.getString(1));
        return e;
    }
}
