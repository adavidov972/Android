package com.example.avidavidov.tictactoe_clientside;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by avi.davidov on 12/01/2017.
 */

public class CheckAvalableUsersThread extends Thread {

    List <Users> avalableUsers = null;
    OnChangeUserListListiner userListListiner;
    boolean go = true;
    String userName, password;
    FragmentAddUserOrLogin fragmentAddUserOrLogin;

    public CheckAvalableUsersThread(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public CheckAvalableUsersThread (){

    }

    public void setUserListListiner(OnChangeUserListListiner userListListiner) {
        this.userListListiner = userListListiner;
    }

    @Override
    public void run() {

        while (go) {

            try {

                //URL url = new URL("http://10.0.2.2:8080/TicTacToeServlet?action=check&userName=avi&password=1234");
                URL url = new URL(MainActivity.BASE_URL + "?action=check&userName=" + userName + "&password=" + password);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setUseCaches(false);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[512];
                int actuallyRead;
                StringBuilder stringBuilder = new StringBuilder();
                while ((actuallyRead = inputStream.read(buffer)) != -1)
                    stringBuilder.append(new String(buffer, 0, actuallyRead));
                if (stringBuilder.length() != 0) {
                    String [] usersArray = stringBuilder.toString().split("&");
                    avalableUsers = new ArrayList<Users>();
                    for (int i = 0; i < usersArray.length; i++) {
                        avalableUsers.add(new Users(usersArray[i],null));
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
                e.printStackTrace();
            }
        }
    }

    public void stopThread () {
        go=false;
    }

    public void startThread () {
        go=true;
    }

}

interface OnChangeUserListListiner {

    public void OnChangeUserList(List <Users> avalableUsers);
}

