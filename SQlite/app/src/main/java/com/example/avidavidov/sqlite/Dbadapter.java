package com.example.avidavidov.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by avi.davidov on 25/01/2017.
 */

public class DbAdapter {

    static final String KEY_PERSON_ID = "PersonId";
    static final String KEY_FIRST_NAME = "FirstName";
    static final String KEY_LAST_NAME = "LastName";
    static final String KEY_BIRTH_YEAR = "BirthYear";
    static final String DATABASE_NAME = "MyDB.db";
    static final String TABLE_NAME = "Persons";
    static final int DATABASE_VERSION = 1;

    static final String CREATE = "CREATE TABLE users (userNo INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,`userName` " +
            "TEXT NOT NULL UNIQUE,`password` INTEGER NOT NULL)";

    static final String TABLE_PERSONS_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + KEY_PERSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_FIRST_NAME + " TEXT NOT NULL, "
            + KEY_LAST_NAME + " TEXT, "
            + KEY_BIRTH_YEAR + " INTEGER)";

    private Context context;
    private SQLiteDatabase db;
    private DatabaseHelper helper;

    public DbAdapter(Context context) {
        this.context = context;
        helper = new DatabaseHelper(context);
    }


    public void Open () {
        if (db==null)
            db = helper.getWritableDatabase();
    }
    public void Close () {
        if (db != null) {
            db.close();
            db=null;
            helper.close();
        }
    }

    public Cursor getPersons (){
        if (db==null)
            return null;
        return db.query(TABLE_NAME, new String[] {KEY_PERSON_ID, KEY_FIRST_NAME, KEY_LAST_NAME, KEY_BIRTH_YEAR}
                , null, null, null,null, KEY_PERSON_ID);
    }

    public boolean updatePerson (int personId, String firstName, String lastName, int birthYear){
        if (db==null)
        return false;
        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME,firstName);
        values.put(KEY_LAST_NAME,lastName);
        values.put(KEY_BIRTH_YEAR,birthYear);

        return db.update(TABLE_NAME, values, KEY_PERSON_ID + "=", null) >0;

    }

    public long insertPerson (String firstName, String lastName, int birthYear){
        if (db ==null)
            return -1L;
        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME,firstName);
        values.put(KEY_LAST_NAME,lastName);
        values.put(KEY_BIRTH_YEAR, birthYear);
        return db.insert(TABLE_NAME,null ,values);

    }

    public boolean deletePerson(long personId){

        if (db==null)
            return false;
        return db.delete(TABLE_NAME, KEY_PERSON_ID + "=?", new String [] {String.valueOf(personId)}) >0;
    }


 private static class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_PERSONS_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXITS");
        onCreate(db);
    }
}


}
