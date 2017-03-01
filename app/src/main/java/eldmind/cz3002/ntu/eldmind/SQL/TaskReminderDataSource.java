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
            EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME, EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING};

    public TaskReminderDataSource(Context context) {
        dbHelper = new EldmindSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }

    public boolean createNewTask(TaskReminder tr) {
        ContentValues values = new ContentValues();
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE, tr.getTitle());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC, tr.getDesc());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME, tr.getDueTime().getTimeInMillis());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING, tr.getRecurring());
        try{
            long insertId = database.insert(EldmindSQLiteHelper.TABLE_TaskReminder, null, values);
            return true;
        }catch(SQLException ex){
            Log.d("TaskReminderDataSource", "CreateNewTask error msg" + ex.getMessage());
            return false;
        }

    }

    public boolean updateTask(TaskReminder tr, TaskReminder otr) {
        //TODO complete update task
        ContentValues values = new ContentValues();
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE, tr.getTitle());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_DESC, tr.getDesc());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_DUETIME, tr.getDueTime().getTimeInMillis());
        values.put(EldmindSQLiteHelper.COLUMN_TaskReminder_RECURRING, tr.getRecurring());

        try {
            String selection = EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE + " ==" + otr.getTitle();
            int count = database.update(EldmindSQLiteHelper.TABLE_TaskReminder, values, selection, null);
        } catch (SQLException ex) {
            Log.d("TaskReminderDataSource", "updateTask error msg" + ex.getMessage());
            return false;
        }

        return true;
    }

    public void deleteTaskReminder(TaskReminder tr) {
        database.delete(EldmindSQLiteHelper.TABLE_TaskReminder,
                        EldmindSQLiteHelper.COLUMN_TaskReminder_TITLE + " = '" + tr.getTitle() + "' AND " +
                        EldmindSQLiteHelper.COLUMN_TaskReminder_DESC + " = '" + tr.getDesc()+"' ",
                null);
    }

    public List<TaskReminder> getAllTaskReminder() {
        List<TaskReminder> trs = new ArrayList<TaskReminder>();

        Cursor cursor = database.query(EldmindSQLiteHelper.TABLE_TaskReminder,
                allColumns, null, null, null, null, null);

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
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(cursor.getLong(3));
        tr.setDueTime(c);
        tr.setRecurring(cursor.getString(4));
        return tr;
    }
}
