package com.example.avidavidov.sqlite;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    DbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbAdapter = new DbAdapter(this);

        dbAdapter.Open();
        long person1 = dbAdapter.insertPerson("Avi", "Davidov", 1974);

        Cursor c = null;
        try {
            c = dbAdapter.getPersons();
            while (c.moveToNext()) {
                int personId = c.getInt(0);
                String firstName = c.getString(1);
                String lastName = c.getString(2);
                int birthYera = c.getInt(3);
                Log.d("Avi", personId + " " + " firstName" + " " + lastName + " " + birthYera);
            }
        }finally {
            if (c!=null)
                c.close();
        }
    }
}
