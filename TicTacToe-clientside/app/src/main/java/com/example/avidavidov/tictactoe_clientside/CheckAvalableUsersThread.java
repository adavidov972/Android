package com.example.avidavidov.tictactoe_clientside;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by avi.davidov on 12/01/2017.
 */

public class CheckAvalableUsersThread extends Thread {

    List<Users> avalableUsers = null;
    OnChangeUserListListiner userListListiner;
    boolean go = true;
    String userName, password;
    FragmentAddUserOrLogin fragmentAddUserOrLogin;
    int clientCounter;

    public CheckAvalableUsersThread(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public void setUserListListiner(OnChangeUserListListiner userListListiner) {
        this.userListListiner = userListListiner;
    }

    @Override
    public void run() {

        while (go) {
            HttpURLConnection connection=null;
            InputStream inputStream = null;
            try {

                //URL url = new URL("http://10.0.2.2:8080/TicTacToeServlet?action=check&userName=avi&password=1234");
                URL url = new URL(MainActivity.BASE_URL + "?action=check&userName=" + userName + "&password=" + password
                        +"&counter="+clientCounter);
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

                if (!result.equals("no change")) {
                    String [] usersAndCounterArray = result.split("~");
                    String[] usersArray = usersAndCounterArray[0].split("&");
                    int currentCounter = Integer.valueOf(usersAndCounterArray[1]);
                    clientCounter = currentCounter;
                    avalableUsers = new ArrayList<Users>();
                    for (int i = 0; i < usersArray.length; i++) {
                        avalableUsers.add(new Users(usersArray[i], null));
                    }
                    if (userListListiner != null)
                        userListListiner.OnChangeUserList(avalableUsers);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            finally {
                if (inputStream != null)
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (connection != null)
                    connection.disconnect();
            }
        }
    }

    public void stopThread() {
        go = false;
    }

    public void startThread() {
        go = true;
    }

    interface OnChangeUserListListiner {

        public void OnChangeUserList(List<Users> avalableUsers);
    }

}



