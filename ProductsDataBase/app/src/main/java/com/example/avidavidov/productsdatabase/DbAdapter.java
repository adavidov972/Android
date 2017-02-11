package com.example.avidavidov.productsdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by avi.davidov on 29/01/2017.
 */

public class DbAdapter {

    private Context context;
    private SQLiteDatabase db;
    private Database helper;

    public DbAdapter(Context context){
        this.context = context;
    }
}
