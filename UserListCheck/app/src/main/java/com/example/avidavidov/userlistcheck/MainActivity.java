package com.example.avidavidov.userlistcheck;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {

    TextView lblUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lblUserList = (TextView) findViewById(R.id.lblUserList);
    }

    public void btnCheckUsers(View view) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                List userList = new LinkedList();

                try {
                    URL url = new URL("http://10.0.2.2:8080/TicTacToeServlet?action=check&userName=avi&password=1234");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setUseCaches(false);
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    int actuallyRead;
                    byte [] buffer = new byte[64];
                    while ((actuallyRead = inputStream.read(buffer))!= -1)
                        result = new String(buffer,0,actuallyRead);
                    String userAndUser [] = result.split("&");
                    for (int i = 0; i < userAndUser.length; i++) {
                        userList.add(userAndUser[i]);
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return userList.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                lblUserList.setText(result);
            }
        }.execute();
    }
}
