package com.example.avidavidov.tictactoe_clientside;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends Activity {

    public static final int CHECKAVALABLEPLAYERS = 100;
    FragmentManager fragmentManager = getFragmentManager();
    FragmentAddUserName userDialog;
    Handler handler;
    //public static final String BASE_URL = "http://104.155.154.50/TicTacToeServlet";
    public static final String BASE_URL = "http://10.0.2.2:8080/TicTacToeServlet";

    boolean isOnGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDialog = new FragmentAddUserName();
        userDialog.setCancelable(false);
        userDialog.show(fragmentManager, "Add user / Login");

//        while (!isOnGame) {
//
//            handler = new Handler();
//            handler.post(new CheckAvalableUsersThread());
//        }

    }
}
