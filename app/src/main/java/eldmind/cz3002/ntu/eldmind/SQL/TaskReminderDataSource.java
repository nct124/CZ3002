package eldmind.cz3002.ntu.eldmind.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import eldmind.cz3002.ntu.eldmind.model.TaskReminder;

/**
 * Created by n on 11/2/2017.
 */

public class TaskReminderDataSource {
    // Database fields
    private SQLiteDatabase database;
    private EldmindSQLiteHelper dbHelper;
    private String[] allColumns = { EldmindSQLiteHelper.COLUMN_TaskReminder_ID,
            EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE,EldmindSQLiteHelper.COLUMN_TaskReminder_DESC,
            EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING,
            EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME,//for SINGLE
            EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYDAY,EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYTIME,//for weekly
            EldmindSQLiteHelper.COLUMN_TaskReminder_STATUS};

    private String TAG = "TaskReminderDataSource";

    public TaskReminderDataSource(Context context) {
        dbHelper = new EldmindSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }

    public int createNewTask(TaskReminder tr) {
        ContentValues values = new ContentValues();
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE, tr.getTitle());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC, tr.getDesc());
        if(tr.getDueTime()!=null){
            values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME, tr.getDueTime().getTimeInMillis());
        }
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING, tr.getRecurring());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYDAY, tr.getWeeklyDay());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYTIME, tr.getWeeklyTime());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_STATUS, tr.getStatus());
        try{
            long insertId = database.insert(EldmindSQLiteHelper.TABLE_TaskReminder, null, values);
            Log.d(TAG, "CreateNewTask my insert id" + insertId);
            return (int)insertId;
        }catch(SQLException ex){
            Log.d(TAG, "CreateNewTask error msg" + ex.getMessage());
            return -1;
        }

    }

    public boolean updateTask(TaskReminder tr, String id) {
        ContentValues values = new ContentValues();
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE, tr.getTitle());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC, tr.getDesc());
        if(tr.getDueTime()!=null){
            values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME, tr.getDueTime().getTimeInMillis());
        }
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING, tr.getRecurring());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYDAY, tr.getWeeklyDay());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_WEEKLYTIME, tr.getWeeklyTime());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_STATUS, tr.getStatus());
        try {
            String selection = EldmindSQLiteHelper.COLUMN_TaskReminder_ID + " = " + id;
            int count = database.update(
                    EldmindSQLiteHelper.TABLE_TaskReminder,
                    values,
                    selection,
                    null);
            Log.d(TAG, "updateTask success id  ====>" + id);
        } catch (SQLException ex) {
            Log.d(TAG, "updateTask error msg" + ex.getMessage());
            return false;
        }
        return true;
    }

    public void deleteTask(String id) {
        try {
            /*database.delete(
                    EldmindSQLiteHelper.TABLE_TaskReminder,
                    EldmindSQLiteHelper.COLUMN_TaskReminder_ID + " = " + id,
                    null);*/
            ContentValues values = new ContentValues();
            values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_STATUS, "DISABLED");
            String selection = EldmindSQLiteHelper.COLUMN_TaskReminder_ID + " = " + id;
            database.update(
                    EldmindSQLiteHelper.TABLE_TaskReminder,
                    values,
                    selection,
                    null);
            Log.d(TAG, "DeleteTask Success id ==> " + id);
        } catch (SQLException ex) {
            Log.d(TAG, "DeleteTask SQLException ==>" + ex.getMessage() + "id==>" + id);
        }

    }
/*
    public void deleteTaskReminder2(TaskReminder tr) { //TODO previous method of deleting task. Not sure if its still useful
        database.delete(EldmindSQLiteHelper.TABLE_TaskReminder,
                        EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE + " = '" + tr.getTitle() + "' AND " +
                        EldmindSQLiteHelper.COLUMN_TaskReminder_DESC + " = '" + tr.getDesc()+"' ",
                null);

    }
*/
    public List<TaskReminder> getAllTaskReminder() {
        List<TaskReminder> trs = new ArrayList<TaskReminder>();

        /*Cursor cursor = database.query(EldmindSQLiteHelper.TABLE_TaskReminder,
                allColumns, null, null, null, null, null);*/
        String selection = EldmindSQLiteHelper.COLUMN_TaskReminder_STATUS + " = 'ENABLED'";
        Cursor cursor = database.query(EldmindSQLiteHelper.TABLE_TaskReminder,allColumns,selection,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TaskReminder tr = cursorToTaskReminder(cursor);
            trs.add(tr);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return trs;
    }

    private TaskReminder cursorToTaskReminder(Cursor cursor) {
        TaskReminder tr = new TaskReminder();
        tr.setId(cursor.getInt(0));
        tr.setTitle(cursor.getString(1));
        tr.setDesc(cursor.getString(2));
        tr.setRecurring(cursor.getString(3));
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(cursor.getLong(4));
        tr.setDueTime(c);
        tr.setWeeklyDay(cursor.getString(5));
        tr.setWeeklyTime(cursor.getString(6));
        tr.setStatus(cursor.getString(7));
        return tr;
    }
}
