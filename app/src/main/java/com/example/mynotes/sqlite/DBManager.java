package com.example.mynotes.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.mynotes.models.NoteModel;

public class DBManager {
    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    //add a note
    public void addNote(NoteModel noteModel) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_TITLE, noteModel.getTitle());
        values.put(DatabaseHelper.KEY_CONTENT, noteModel.getContent());

        database.insert(DatabaseHelper.TABLE_NOTES, null, values);
        database.close();
    }

    //get all note
    public Cursor getAllNote() {
        String[] columns = new String[]{DatabaseHelper.KEY_ID, DatabaseHelper.KEY_TITLE, DatabaseHelper.KEY_CONTENT};
        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    //update a note
    public int updateNote(NoteModel noteModel) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_TITLE, noteModel.getTitle());
        values.put(DatabaseHelper.KEY_CONTENT, noteModel.getContent());

        // updating row
        return database.update(DatabaseHelper.TABLE_NOTES, values, DatabaseHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(noteModel.getId())});
    }

    //delete a note
    public void deleteModel(NoteModel noteModel) {
        database.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(noteModel.getId())});
        database.close();
    }
}
