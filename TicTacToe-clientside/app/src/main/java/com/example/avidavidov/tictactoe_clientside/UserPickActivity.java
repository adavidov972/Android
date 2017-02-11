package com.example.avidavidov.tictactoe_clientside;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class UserPickActivity extends Activity implements OnChangeUserListListiner {
    ListView usersListView;
    UserListAdapter userAdapter;
    List<Users> avalableUsers;
    String userName, password;
    String pickedUserName;
    CheckAvalableUsersThread thread;
    FragmentPleaseWait pleaseWaitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pick);

        thread = new CheckAvalableUsersThread();
        thread.setUserListListiner(this);
        avalableUsers = new ArrayList();
        avalableUsers.add(new Users("Shimon", null));
        usersListView = (ListView) findViewById(R.id.lstAvalableUsers);
        userAdapter = new UserListAdapter(this, avalableUsers);
        usersListView.setAdapter(userAdapter);

        Intent intent = getIntent();

        if (intent.getStringExtra(MainActivity.USER_NAME) != null)
            userName = intent.getStringExtra(MainActivity.USER_NAME);

        if (intent.getStringExtra(MainActivity.PASSWORD) != null)
            password = intent.getStringExtra(MainActivity.PASSWORD);


        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();
                pleaseWaitDialog = new FragmentPleaseWait();
                pleaseWaitDialog.setCancelable(false);
                pleaseWaitDialog.show(fragmentManager, null);
                pickedUserName = avalableUsers.get(position).getUserName().toString();

                new AsyncTask<String, Void, String>() {

                    InputStream inputStream = null;
                    String result = "";

                    @Override
                    protected String doInBackground(String... params) {

                        String userName = params[0];
                        String password = params[1];
                        String userPicked = params[2];

                        try {

                            URL url = new URL(MainActivity.BASE_URL + "?action=pickUser" + "&userName=" + userName + "&password=" + password
                                    + "&userPicked=" + userPicked);
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestMethod("GET");
                            urlConnection.setUseCaches(false);
                            urlConnection.connect();
                            inputStream = urlConnection.getInputStream();
                            byte[] buffer = new byte[64];
                            int actuallyRead;
                            while ((actuallyRead = inputStream.read(buffer)) != -1)
                                result = new String(buffer, 0, actuallyRead);

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return result;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if (result.equals("ok")) {
                            thread.stopThread();
                            setResult(MainActivity.RESULT_OK);
                            pleaseWaitDialog.dismiss();
                            finish();
                        } else {
                            Toast.makeText(UserPickActivity.this, "User havn't Picked. Please pick another user", Toast.LENGTH_SHORT).show();
                            pleaseWaitDialog.dismiss();

                        }
                    }
                }.execute(userName, password, pickedUserName);


            }
        });


    }

    @Override
    public void OnChangeUserList(List avalableUsers) {
        this.avalableUsers = avalableUsers;
        userAdapter.notifyDataSetChanged();
    }
}
