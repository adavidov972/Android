package com.example.avidavidov.ninepuzzle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton button = (ImageButton) findViewById(R.id.Button1);


    }
}
