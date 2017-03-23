package com.example.avidavidov.tictactoe_clientside;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by avi.davidov on 25/02/2017.
 */

public class FragmentSomeonePickedYou extends DialogFragment {

    TextView pickedYou;
    Button btnApprove, btnDismiss;
    String userPicked = "";
    int gameNumber;
    String userName, password;
    boolean isApproved;
    OnUserApprovedListiner listiner;

    public void setUserPicked(String userPicked) {
        this.userPicked = userPicked;
    }

    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isApproved() {
        return isApproved;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_someone_picked_you, container);
        pickedYou = (TextView) view.findViewById(R.id.lblPickedYou);
        pickedYou.setText(userPicked + " wants to play with you");

        btnApprove = (Button) view.findViewById(R.id.btnApprove);
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream inputStream = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(MainActivity.BASE_URL + "?action=approvePicking&userName=" + userName + "&password=" + password
                            + "&approveAnswer=ok");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(false);
                    connection.connect();
                    inputStream = connection.getInputStream();
                    int actuallyRead;
                    byte[] buffer = new byte[32];
                    String result = "";
                    while ((actuallyRead = inputStream.read(buffer)) != -1) {
                        result = new String(buffer, 0, actuallyRead);
                    }
                    if (result.equals("start Game"))
                        dismiss();
                    isApproved=true;
                    if (listiner!=null)
                        listiner.onUserApproved(userName,userPicked,gameNumber);

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
            }
        });

        btnDismiss = (Button) view.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream inputStream = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(MainActivity.BASE_URL + "?action=approvePicking&userName=" + userName + "&password=" + password
                            + "&approveAnswer=dismiss");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(false);
                    connection.connect();
                    isApproved = false;
                    dismiss();

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
            }
        });

        return view;
    }
    interface OnUserApprovedListiner {
        public void onUserApproved (String userName,String userPicked, int gameNumber);
    }
}
