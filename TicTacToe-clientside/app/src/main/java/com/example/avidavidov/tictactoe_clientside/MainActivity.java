package com.example.avidavidov.tictactoe_clientside;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.List;

public class MainActivity extends Activity implements FragmentAddUserOrLogin.OnLoggingInListiner {

    public static final int CHECKAVALABLEPLAYERS = 100;
    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "password";
    public static final int RESULT_OK = 1;
    FragmentManager fragmentManager = getFragmentManager();
    FragmentAddUserOrLogin userDialog;
    String userName, password;
    CheckAvalableUsersThread checkThread;
    //public static final String BASE_URL = "http://104.198.183.172/TicTacToeServlet";
    public static final String BASE_URL = "http://10.0.2.2:8080/TicTacToeServlet";
    boolean isOnGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        userDialog = new FragmentAddUserOrLogin();
        userDialog.setCancelable(false);
        userDialog.setListiner(this);
        userDialog.show(fragmentManager, "Add user / Login");
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkThread.interrupt();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startThread(userName,password);
    }


    private void startThread (String userName, String password) {
        checkThread = new CheckAvalableUsersThread(userName,password);
        checkThread.start();
    }




    @Override
    public void onLoggingIn(String userName, String password) {
        this.userName = userName;
        this.password = password;
        startThread(userName,password);

        Intent intent = new Intent(this,UserPickActivity.class);
        intent.putExtra(USER_NAME,userName);
        intent.putExtra(PASSWORD,password);
        startActivityForResult(intent, RESULT_OK);


    }
}
