package com.avidavidov.avidavidov.tictactoe_clientside;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.google.android.gms.internal.zzt.TAG;

public class MainActivity extends Activity implements FragmentAddUserOrLogin.OnLoggingInListiner {

    //public static final String BASE_URL = "http://104.154.155.251/TicTacToeServlet";
    //public static final String BASE_URL = "http://10.100.102.24:8080/TicTacToeServlet";
    //public static final String BASE_URL = "http://10.0.2.2:8080/TicTacToeServlet";
    public static final String BASE_URL = "http://104.154.94.113/TicTacToe/TicTacToeServlet";

    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "password";
    public static final int RESULT_OK = 1;
    public static final String GAMENUMBER = "GAMENUMBER";
    public static final String X_PLAYER = "xPlayer";
    public static final String CRCL_PLAYER = "crclPlayer";
    public static final String USER_NAME_PREF = "userNamePref";
    public static final String FCM_TOKEN = "FCMToken";
    FragmentManager fragmentManager = getFragmentManager();
    FragmentAddUserOrLogin userDialog;
    private boolean isLogedIn = false;
    SharedPreferences sharedPreferences;
    String userName, password;
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
        String currentToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Current token is : " + currentToken);
        this.userName = userName;
        this.password = password;
        lblConnectedAs.setText("Connected as : " + userName);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, userName);
        editor.putString(PASSWORD, password);
        //if (!sharedPreferences.getString(FCM_TOKEN, currentToken).equals(currentToken)) {
            editor.putString(FCM_TOKEN, currentToken);
            MyFirebaseInstanceIDService firebaseInstanceIDService = new MyFirebaseInstanceIDService ();
            firebaseInstanceIDService.setUserName(userName);
            firebaseInstanceIDService.setPassword(password);
            firebaseInstanceIDService.onTokenRefresh();
        //}

        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;
                String result = "";
                try {
                    URL url = new URL(MainActivity.BASE_URL + "?action=logout"
                            + "&userName=" + userName
                            + "&password=" + password);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setUseCaches(false);
                    urlConnection.connect();

                    inputStream = urlConnection.getInputStream();
                    byte[] buffer = new byte[64];
                    int actuallyRead;
                    while ((actuallyRead = inputStream.read(buffer)) != -1)
                        result = new String(buffer, 0, actuallyRead);
                    inputStream.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

        }.execute();
    }

    public void btnNewGame(View view) {
        if (isLogedIn) {
            Intent intent = new Intent(this, UserPickActivity.class);
            intent.putExtra(USER_NAME, userName);
            intent.putExtra(PASSWORD, password);
            startActivityForResult(intent, RESULT_OK);
        } else {
            Toast.makeText(this, "Please log in", Toast.LENGTH_LONG).show();
        }

    }

    public void btnSwitchUser(View view) {
        userDialog = new FragmentAddUserOrLogin();
        userDialog.setCancelable(false);
        userDialog.setListiner(this);
        userDialog.show(fragmentManager, "Add user / Login");
    }
}

// TODO: 27/05/2017 on OnStop tell the srver that the user is pickable again
// TODO: 27/05/2017 implement onMessageRecieve