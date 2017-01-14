package com.example.avidavidov.tictactoe_clientside;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by avi.davidov on 12/01/2017.
 */

public class CheckAvalableUsersThread extends Thread {

    List<String> avalableUsers = null;


    @Override
    public void run() {

    try {

            URL url = new URL(MainActivity.BASE_URL + "?check");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setUseCaches(false);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[512];
            int actuallyRead;
            StringBuilder stringBuilder = new StringBuilder();
            while ((actuallyRead = inputStream.read()) != -1)
                stringBuilder.append(new String(buffer, 0, actuallyRead));
            if (stringBuilder.length() != 0) {

                String[] usersArray = stringBuilder.toString().split("&");
                for (int i = 0; i < usersArray.length; i++) {
                    avalableUsers.add(usersArray[i]);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
