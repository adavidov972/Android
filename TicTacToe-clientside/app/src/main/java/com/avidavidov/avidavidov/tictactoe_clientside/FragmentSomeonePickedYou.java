package com.avidavidov.avidavidov.tictactoe_clientside;

import android.app.DialogFragment;
import android.os.AsyncTask;
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
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by avi.davidov on 25/02/2017.
 */

public class FragmentSomeonePickedYou extends DialogFragment {

    private TextView pickedYou;
    private Button btnApprove, btnDismiss;
    private String userPicked = "";
    private int gameNumber;
    private String userName, password;
    private OnUserApprovedListiner listiner;

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

    public void setListiner(OnUserApprovedListiner listiner) {
        this.listiner = listiner;
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

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        InputStream inputStream = null;
                        HttpURLConnection connection = null;
                        String result = "";
                        try {
                            URL url = new URL(MainActivity.BASE_URL + "?action=approvePicking&userName=" + userName + "&password=" + password
                                    + "&approveAnswer=approve" + "&gameNumber=" + gameNumber);
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setUseCaches(false);
                            connection.connect();
                            inputStream = connection.getInputStream();
                            int actuallyRead;
                            byte[] buffer = new byte[32];
                            while ((actuallyRead = inputStream.read(buffer)) != -1) {
                                result = new String(buffer, 0, actuallyRead);
                            }

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
                            if (connection != null)
                                connection.disconnect();
                        }
                        return result;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if (result.equals("start Game"))
                            dismiss();
                        if (listiner != null)
                            listiner.onUserApproved(userName, userPicked, gameNumber);
                    }
                }.execute();
            }
        });

        btnDismiss = (Button) view.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        InputStream inputStream = null;
                        HttpURLConnection connection = null;
                        try {
                            URL url = new URL(MainActivity.BASE_URL + "?action=approvePicking&userName=" + userName + "&password=" + password
                                    + "&approveAnswer=dismiss");
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setUseCaches(false);
                            connection.connect();

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
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (listiner!=null)
                            listiner.onUserDismissed();
                        dismiss();
                    }

                }.execute();

            }
        });

        return view;
    }

    interface OnUserApprovedListiner {
        void onUserApproved(String userName, String userPicked, int gameNumber);
        void onUserDismissed();

    }
}
