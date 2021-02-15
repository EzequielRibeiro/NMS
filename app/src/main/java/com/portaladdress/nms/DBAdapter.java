package com.portaladdress.nms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DBAdapter {

    static final String KEY_ID = "id";
    static final String KEY_CODE = "code";
    static final String KEY_COMMENTS = "comments";
    static final String DATABASE_NAME = "nmsencoder";
    static final String DATABASE_TABLE_GLYPHS = "glyphs";
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_CREATE_FAVORITE = "CREATE TABLE IF NOT EXISTS "+DATABASE_TABLE_GLYPHS +"(" +
             KEY_ID + " integer primary key autoincrement," +
             KEY_CODE + " text not null unique ON CONFLICT ABORT," +
             KEY_COMMENTS +  " text);";

    final Context context;
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase db;

    public DBAdapter(Context cont) {
        context = cont;
        dataBaseHelper = new DataBaseHelper(context);
        db = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
        db.close();
    }

    //insert
    public long insertGlyphs(String code, String comments) throws SQLException {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CODE, code);
        initialValues.put(KEY_COMMENTS, comments);
        return db.insert(DATABASE_TABLE_GLYPHS, null, initialValues);
    }

    //delete
    public boolean deleteGlyphs(long id) throws SQLException {

        return db.delete(DATABASE_TABLE_GLYPHS, "id=" + id, null) > 0;

    }

    //retriever all values from database
    public List<Glyphs> getAllValuesGlyphs() throws SQLException {

        List<Glyphs> glyphsArrayList = new LinkedList<Glyphs>();

        Cursor cursor =  db.query(DATABASE_TABLE_GLYPHS,
                new String[]{"id", KEY_CODE, KEY_COMMENTS}, null, null, null, null, "id DESC");

        Glyphs glyphs;

        if (cursor.moveToFirst()) {
            do {
                glyphs = new Glyphs();
                glyphs.setId(cursor.getInt(0));
                glyphs.setGlyphsCode(cursor.getString(1));
                glyphs.setComments(cursor.getString(2));
                glyphsArrayList.add(glyphs);
            } while (cursor.moveToNext());
        }
        return glyphsArrayList;

    }

    public boolean updateGlyphs(int id, String code, String commenst) throws SQLException {

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CODE, code);
        contentValues.put(KEY_COMMENTS, commenst);

        return db.update(DATABASE_TABLE_GLYPHS, contentValues, "id=" + id, null) > 0;

    }

    public int getCount(){
        Cursor cursor;
        int count = 0;

        cursor = db.rawQuery("select count("+KEY_CODE+") from "+ DATABASE_TABLE_GLYPHS,null);
        if(cursor.moveToFirst())
            count = cursor.getInt(0);
        cursor.close();

        return count;


    }

    private static class DataBaseHelper extends SQLiteOpenHelper {


        DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            try {
                sqLiteDatabase.execSQL(DATABASE_CREATE_FAVORITE);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_GLYPHS);
            onCreate(sqLiteDatabase);
        }

    }


}
