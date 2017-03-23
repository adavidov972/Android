package com.example.avidavidov.tictactoe_clientside;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by avi.davidov on 23/03/2017.
 */

public class ifCrclApprovedThread extends Thread {

    private String userName, password;
    private int gameNumber;
    String result = "";


    public ifCrclApprovedThread(String userName, String password, int gameNumber) {
        this.userName = userName;
        this.password = password;
        this.gameNumber = gameNumber;
    }



    @Override
    public void run() {

        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(MainActivity.BASE_URL + "?action=ifCrclApproved" + "&userName=" + userName + "&password=" + password
                    + "&gameNumber=" + Integer.valueOf(gameNumber));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setUseCaches(false);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[32];
            int actuallyRead;
            while ((actuallyRead = inputStream.read(buffer)) != -1)
                result = new String(buffer, 0, actuallyRead);
            inputStream.close();
            urlConnection.disconnect();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
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
    }


    {
        try {
            int threadCounter = 0;
            while (threadCounter < 31) {
                Thread.sleep(1000);
                threadCounter++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
