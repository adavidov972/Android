package com.example.avidavidov.lesson37_sms;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("adrress", "1234 435; 23455");
        intent.putExtra("sms_body", "Black friday");
        intent.setType("vdn.androis-dir/mms-sms");
        startActivity(intent);
    }
}
