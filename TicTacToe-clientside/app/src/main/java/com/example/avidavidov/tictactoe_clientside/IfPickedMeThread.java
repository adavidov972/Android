package com.example.avidavidov.tictactoe_clientside;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by avi.davidov on 25/02/2017.
 */

public class IfPickedMeThread extends Thread {

    String userName;
    String password;
    OnPickingMeListiner listiner;
    private boolean go=true;

    public IfPickedMeThread(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public void setListiner(OnPickingMeListiner listiner) {
        this.listiner = listiner;
    }

    @Override
    public void run() {

        while (go) {

            HttpURLConnection connection = null;
            InputStream inputStream = null;

            try {
                URL url = new URL(MainActivity.BASE_URL + "?action=ifIvPicked" + "&userName=" + userName + "&password=" + password);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                connection.connect();
                inputStream = connection.getInputStream();
                int actuallyRead;
                byte[] buffer = new byte[64];
                String answer = "";
                while ((actuallyRead = inputStream.read(buffer)) != -1)
                    answer = new String(buffer, 0, actuallyRead);
                inputStream.close();
                connection.disconnect();
                String resultWithPicker[] = answer.split("&");
                if (resultWithPicker.length==2) {
                    String result = resultWithPicker[0];
                    String userPicked = resultWithPicker[1];
                    if (result.equals("ok"))

                        if (listiner != null) {
                            listiner.onSomeonePickedMe(userPicked);
                        }
                }

            } catch (
                    MalformedURLException e
                    )

            {
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
                if (connection != null)
                    connection.disconnect();
            }
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread (){
        go=false;
    }

    interface OnPickingMeListiner {
        public void onSomeonePickedMe(String userPicked);
    }
}


