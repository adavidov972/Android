package com.example.avidavidov.tictactoe_clientside;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements FragmentAddUserOrLogin.OnLoggingInListiner {

    //public static final String BASE_URL = "http://104.154.155.251/TicTacToeServlet";
    //public static final String BASE_URL = "http://10.100.102.17:8080/TicTacToeServlet";
    //public static final String BASE_URL = "http://10.0.2.2:8080/TicTacToeServlet";
    public static final String BASE_URL = "http://104.154.94.113/TicTacToeServer/TicTacToeServlet";

    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "password";
    public static final int RESULT_OK = 1;
    public static final String GAMENUMBER = "GAMENUMBER";
    public static final String USERPICKED = "USERPICKED";
    public static final String X_PLAYER = "xPlayer";
    public static final String CRCL_PLAYER = "crclPlayer";
    public static final String USER_NAME_PREF = "userNamePref";
    FragmentManager fragmentManager = getFragmentManager();
    FragmentAddUserOrLogin userDialog;
    private boolean isLogedIn = false;
    SharedPreferences sharedPreferences;
    String userName,password;
    TextView lblConnectedAs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lblConnectedAs = (TextView) findViewById(R.id.lblConnectedAs);
        sharedPreferences = getSharedPreferences(USER_NAME_PREF, MODE_PRIVATE);

        if (sharedPreferences.getString(USER_NAME, null) != null) {
            userName = sharedPreferences.getString(USER_NAME, null);
            password = sharedPreferences.getString(PASSWORD, null);
            onLoggingIn(userName, password);
        } else {

            userDialog = new FragmentAddUserOrLogin();
            userDialog.setCancelable(false);
            userDialog.setListiner(this);
            userDialog.show(fragmentManager, "Add user / Login");
        }
    }

    @Override
    public void onLoggingIn(String userName, String password) {
        isLogedIn = true;
        this.userName = userName;
        this.password = password;
        lblConnectedAs.setText("Connected as : " + userName);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, userName);
        editor.putString(PASSWORD, password);
        editor.commit();


    }

    public void btnNewGame(View view) {
        if (isLogedIn) {
            Intent intent = new Intent(this, UserPickActivity.class);
            intent.putExtra(USER_NAME, userName);
            intent.putExtra(PASSWORD, password);
            startActivityForResult(intent, RESULT_OK);
        }else {
            Toast.makeText(this, "Please log in", Toast.LENGTH_LONG).show();
        }

    }
}
