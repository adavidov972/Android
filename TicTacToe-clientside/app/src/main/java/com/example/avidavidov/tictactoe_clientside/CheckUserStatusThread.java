package com.example.avidavidov.tictactoe_clientside;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by avi.davidov on 12/01/2017.
 */

public class CheckUserStatusThread extends Thread {

    List<Users> avalableUsers = null;
    OnChangeUsersStatusListiner listiner;
    boolean go = true;
    String userName, password;
    FragmentAddUserOrLogin fragmentAddUserOrLogin;
    int clientCounter;
    Map <String, String> qs;
    //ConnectivityManager connectivityManager;
    //NetworkInfo activeNetworkInfo;
    Context context;

    public CheckUserStatusThread(Context context, String userName, String password) {
        this.context = context;
        this.userName = userName;
        this.password = password;
    }

    public void setListiner(OnChangeUsersStatusListiner listiner) {
        this.listiner = listiner;
    }

    @Override
    public void run() {

        while (go) {

            //connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            HttpURLConnection connection = null;
            InputStream inputStream = null;

            //while (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

                try {

                    URL url = new URL(MainActivity.BASE_URL + "?action=checkUsersStatus&userName=" + userName + "&password=" + password
                            + "&counter=" + clientCounter);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(false);
                    connection.connect();
                    inputStream = connection.getInputStream();
                    byte[] buffer = new byte[512];
                    int actuallyRead;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((actuallyRead = inputStream.read(buffer)) != -1)
                        stringBuilder.append(new String(buffer, 0, actuallyRead));
                    String result = stringBuilder.toString();
                    inputStream.close();
                    connection.disconnect();
                    getQuery(result);
                    String answer = qs.get("answer");

                    if (answer != null) {

                        switch (answer) {

                            case "no change":
                                break;

                            case "youHaveBeenPickedBy":
                                int gameNumber = Integer.valueOf(qs.get("gameNumber"));
                                String userPicked = qs.get("userPickedYou");
                                stopThread();
                                if (listiner != null)
                                    listiner.onSomeonePickedMe(userPicked, gameNumber);
                                break;

                            case "change":
                                String userListArray = qs.get("userList");
                                String[] usersArray = userListArray.split("~");
                                int currentCounter = Integer.valueOf(qs.get("currentCounter"));
                                clientCounter = currentCounter;
                                avalableUsers = new ArrayList<>();
                                for (int i = 0; i < usersArray.length; i++) {
                                    avalableUsers.add(new Users(usersArray[i], null));
                                }
                                if (listiner != null)
                                    listiner.OnChangeUserList(avalableUsers);
                                break;

                            default:
                                break;

                        }
                    }

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
                    if (connection != null)
                        connection.disconnect();
                }
            //}
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }
    }

    public void stopThread() {
        interrupt();
        go = false;
    }

    public void getQuery(String query) {

        if (query == null || query.isEmpty())
            return;
        String[] keysAndValues = query.split("&");
        qs = new HashMap<>();

        for (int i = 0; i < keysAndValues.length; i++) {
            String[] keyAndValue = keysAndValues[i].split("=");
            if (keyAndValue.length != 2)
                continue;
            qs.put(keyAndValue[0], (keyAndValue[1]));

        }
    }

    interface OnChangeUsersStatusListiner {

        void OnChangeUserList(List<Users> avalableUsers);
        void onSomeonePickedMe(String userPicked, int gameNumber);
    }

}



