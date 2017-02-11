package com.example.avidavidov.sharedpreferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static final String MYPREF = "mypref";
    EditText txtStoShow;
    Button btnToShow;
    TextView lblToShow;
    String showTxt;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(MYPREF, MODE_PRIVATE);
        sharedPreferences.edit();
        editor = sharedPreferences.edit();
        txtStoShow = (EditText) findViewById(R.id.txtToShow);
        lblToShow = (TextView) findViewById(R.id.lblShowText);

    }

    public void btnShowText(View view) {

        showTxt = txtStoShow.getText().toString();
        lblToShow.setText(showTxt);
        editor.putString("txt", showTxt);
        editor.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        lblToShow.setText(sharedPreferences.getString("txt", null));

    }
}
