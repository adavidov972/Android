package com.avidavidov.avidavidov.tictactoe_clientside;

import android.os.AsyncTask;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by avi.davidov on 30/04/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    String userName, password;

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {

        new AsyncTask<String, Void, String>() {
            @Override


            protected String doInBackground(String... params) {

                String token = params [0];
                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;
                String result = "";

                try {
                    URL url = new URL(MainActivity.BASE_URL + "?action=FcmTokenRefresh"
                            + "&userName=" + userName
                            + "&password=" + password
                            + "&FCMToken=" + token);
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
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null)
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (!result.equals("ok")){

                }
            }
        }.execute(token);

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
