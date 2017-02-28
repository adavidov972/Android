package com.example.avidavidov.tictactoe_clientside;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by avi.davidov on 05/01/2017.
 */

public class FragmentAddUserOrLogin extends DialogFragment {

    EditText txtuserName, txtpassword;
    Button btnAddUser, btnLogin;
    String userName, password;
    OnLoggingInListiner listiner;


    public void setListiner(OnLoggingInListiner listiner) {
        this.listiner = listiner;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_username, container);
        txtuserName = (EditText) view.findViewById(R.id.txtuserName);
        txtpassword = (EditText) view.findViewById(R.id.txtPassword);
        btnAddUser = (Button) view.findViewById(R.id.btnAddUser);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);


        View.OnClickListener userFragmrntBtnListiner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtuserName == null || txtpassword == null) {
                    Toast.makeText(getActivity(), "Please enter user name and password", Toast.LENGTH_SHORT).show();
                }

                password = txtpassword.getText().toString();
                userName = txtuserName.getText().toString();
                String action = v.getTag().toString();

                if (userName.isEmpty() || userName.length() < 3) {
                    Toast.makeText(getActivity(), "Pleser enter valid User name (Min. 3 chars)", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty() || password.length() < 4) {
                    Toast.makeText(getActivity(), "Please enter valid password (min. 4 chars)", Toast.LENGTH_SHORT).show();
                    return;
                }
                btnAddUser.setClickable(false);
                btnLogin.setClickable(false);

                new AsyncTask<String, Void, String>() {

                    @Override
                    protected String doInBackground(String... params) {

                        String userName = params[0];
                        String password = params[1];
                        String action = params[2];
                        String result = "";
                        HttpURLConnection urlConnection = null;
                        InputStream inputStream = null;

                        try {
                            URL url = new URL(MainActivity.BASE_URL + "?action=" + action
                                    + "&userName=" + userName
                                    + "&password=" + password);
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

                        String[] resultArray = new String[2];
                        resultArray[0] = result;

                        return result + "&" + action;

                    }

                    @Override
                    protected void onPostExecute(String resultArray) {

                        String[] resultAndAction = resultArray.split("&");
                        String result = resultAndAction[0];
                        String action = resultAndAction[1];

                        if (result.equals("ok")) {
                            switch (action) {
                                case "signUp":
                                    Toast.makeText(getActivity(), "User added", Toast.LENGTH_SHORT).show();
                                    break;
                                case "login":
                                    Toast.makeText(getActivity(), "Login success", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            if (listiner != null)
                                listiner.onLoggingIn(userName, password);
                            dismiss();

                        } else {
                            Toast.makeText(getActivity(), "Login/Add user failed", Toast.LENGTH_SHORT).show();

                        }
                        btnAddUser.setClickable(true);
                        btnLogin.setClickable(true);
                    }
                }.execute(userName, password, action);
            }
        };

        btnAddUser.setOnClickListener(userFragmrntBtnListiner);
        btnLogin.setOnClickListener(userFragmrntBtnListiner);

        return view;
    }


    interface OnLoggingInListiner {
        public void onLoggingIn(String userName, String password);
    }

}