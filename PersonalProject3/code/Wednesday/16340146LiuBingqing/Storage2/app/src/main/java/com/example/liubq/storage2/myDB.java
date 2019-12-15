package com.example.liubq.storage2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class myDB extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "USER";

    public myDB(Context context) {
        super(context, "database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                + TABLE_NAME
                + " (_id INTEGER PRIMARY KEY, name TEXT, password TEXT, text TEXT, img TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE if not exists "
                + "COMMENTS"
                + " (_id INTEGER PRIMARY KEY, name TEXT, text TEXT, time TEXT, img TEXT, num INTEGER)";
        sqLiteDatabase.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE if not exists "
                + "LIKE_TABLE"
                + " (_id INTEGER PRIMARY KEY, name TEXT, time TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int ii) {

    }
}
