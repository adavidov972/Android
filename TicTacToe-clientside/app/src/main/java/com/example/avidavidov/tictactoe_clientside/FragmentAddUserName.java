package com.example.avidavidov.tictactoe_clientside;

import android.app.DialogFragment;
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

/**
 * Created by avi.davidov on 05/01/2017.
 */

public class FragmentAddUserName extends DialogFragment {

    //public static final String BASE_URL = "http://104.155.154.50/TicTacToeServlet";

    EditText txtuserName, txtpassword;
    Button btnAddUser, btnLogin;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_username, container);

        txtuserName = (EditText) view.findViewById(R.id.txtuserName);
        txtpassword = (EditText) view.findViewById(R.id.txtPassword);
        btnAddUser = (Button) view.findViewById(R.id.btnAddUser);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);

        View.OnClickListener userFragmrntBtnListiner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtuserName == null || txtpassword == null) {
                    Toast.makeText(getActivity(), "Please ent user name and password", Toast.LENGTH_SHORT).show();
                }

                String password = txtpassword.getText().toString();
                String userName = txtuserName.getText().toString();
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
                if (result == "OK") {
                    switch (action) {
                        case "signUp":
                            Toast.makeText(getActivity(), "User added", Toast.LENGTH_SHORT).show();
                            break;
                        case "login":
                            Toast.makeText(getActivity(), "Login success", Toast.LENGTH_SHORT).show();
                            break;

                    }
                    dismiss();
                } else {

                    Toast.makeText(getActivity(), "Login/Add user failed", Toast.LENGTH_SHORT).show();
                }
                btnAddUser.setClickable(true);
                btnLogin.setClickable(true);

            }
        };


        btnAddUser.setOnClickListener(userFragmrntBtnListiner);
        btnLogin.setOnClickListener(userFragmrntBtnListiner);

        return view;

    }
}
