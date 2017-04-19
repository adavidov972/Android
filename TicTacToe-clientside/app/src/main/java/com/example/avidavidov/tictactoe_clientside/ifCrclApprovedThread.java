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
    OnCrclApprovedListiner listiner;
    boolean go = true;


    public ifCrclApprovedThread(String userName, String password, int gameNumber) {
        this.userName = userName;
        this.password = password;
        this.gameNumber = gameNumber;
    }


    public void setListiner(OnCrclApprovedListiner listiner) {
        this.listiner = listiner;
    }

    @Override
    public void run() {

        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        int threadCounter = 0;

        while (go) {

            try {

                threadCounter++;
                URL url = new URL(MainActivity.BASE_URL + "?action=ifCrclApproved" + "&userName=" + userName + "&password=" + password
                        + "&gameNumber=" + gameNumber);
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
            }

            if (result.equals("approved"))
                listiner.onCrclApproved(gameNumber);

            if (threadCounter == 30)
                listiner.onCrclDismissedOrNotAnswering();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
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
    }

    public void stopThread() {
        go = false;
    }

    public void startThread() {
        go = true;
    }

    interface OnCrclApprovedListiner {

        public void onCrclApproved(int gameNumber);
        public void onCrclDismissedOrNotAnswering();

    }

}
