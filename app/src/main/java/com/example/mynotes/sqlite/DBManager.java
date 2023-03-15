package com.example.mynotes.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mynotes.models.NoteModel;

import java.util.ArrayList;
import java.util.List;

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
    }

    //get all note
//    public Cursor getAllNote() {
//        String[] columns = new String[]{DatabaseHelper.KEY_ID, DatabaseHelper.KEY_TITLE, DatabaseHelper.KEY_CONTENT};
//        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES, columns, null, null, null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//        }
//        return cursor;
//    }

    public List<NoteModel> getAllNotes() {
        List<NoteModel> noteModelList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_NOTES;

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                NoteModel noteModel = new NoteModel();
                noteModel.setId(cursor.getString(0));
                noteModel.setTitle(cursor.getString(1));
                noteModel.setContent(cursor.getString(2));

                noteModelList.add(noteModel);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return noteModelList;
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
    public void deleteNote(NoteModel noteModel) {
        open();
        database.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(noteModel.getId())});
    }
}
