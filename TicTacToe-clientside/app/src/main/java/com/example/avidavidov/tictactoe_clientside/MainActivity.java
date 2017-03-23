package com.example.avidavidov.tictactoe_clientside;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity implements FragmentAddUserOrLogin.OnLoggingInListiner {

    //public static final String BASE_URL = "http://104.198.183.172/TicTacToeServlet";
    public static final String BASE_URL = "http://10.0.2.2:8080/TicTacToeServlet";
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
    boolean isOnGame = false;
    SharedPreferences sharedPreferences;
    TextView lblWelcome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lblWelcome = (TextView) findViewById(R.id.lblwelcome);
        sharedPreferences = getSharedPreferences(USER_NAME_PREF, MODE_PRIVATE);

        if (sharedPreferences.getString(USER_NAME,null) != null) {
            String userName = sharedPreferences.getString(USER_NAME, null);
            String password = sharedPreferences.getString(PASSWORD, null);
            lblWelcome.setText(userName);
            onLoggingIn(userName, password);
        } else {

            userDialog = new FragmentAddUserOrLogin();
            userDialog.setCancelable(false);
            userDialog.setListiner(this);
            userDialog.show(fragmentManager, "Add user / Login");
            lblWelcome.setText(userDialog.getUserName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {

            String userName = data.getStringExtra(USER_NAME);
            String password = data.getStringExtra(PASSWORD);
            String userPicked = data.getStringExtra(USERPICKED);
            int gameNumber = data.getIntExtra(GAMENUMBER,-1);

            Intent gameIntent = new Intent(this, Game_Activity.class);
            gameIntent.putExtra(X_PLAYER, userName);
            gameIntent.putExtra(CRCL_PLAYER, userPicked);
            gameIntent.putExtra(USER_NAME, userName);
            gameIntent.putExtra(GAMENUMBER,gameNumber);
            startActivityForResult(gameIntent, RESULT_OK);
        }
    }

    @Override
    public void onLoggingIn(String userName, String password) {

        Intent intent = new Intent(this, UserPickActivity.class);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME,userName);
        editor.putString(PASSWORD,password);
        editor.commit();
        intent.putExtra(USER_NAME, userName);
        intent.putExtra(PASSWORD, password);
        startActivityForResult(intent, RESULT_OK);

    }

}
