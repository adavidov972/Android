package com.example.avidavidov.tictactoe_clientside;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserPickActivity extends Activity implements CheckAvalableUsersThread.OnChangeUserListListiner, IfPickedMeThread.OnPickingMeListiner {
    private ListView usersListView;
    private UserListAdapter userAdapter;
    private List<Users> avalableUsers;
    private String userName, password;
    private String pickedUserName;
    private CheckAvalableUsersThread checkAvalableThread;
    private IfPickedMeThread ifPickedMeThread;
    private FragmentPleaseWait pleaseWaitDialog;
    private FragmentSomeonePickedYou pickedDialog;
    TextView lblConnectedAs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pick);
        lblConnectedAs = (TextView) findViewById(R.id.lblConnectedAs);

        Intent intent = getIntent();
        if (intent.getStringExtra(MainActivity.USER_NAME) != null)
            userName = intent.getStringExtra(MainActivity.USER_NAME);
        if (intent.getStringExtra(MainActivity.PASSWORD) != null)
            password = intent.getStringExtra(MainActivity.PASSWORD);

        lblConnectedAs.setText("Connected as user name : "+userName);
        startCheckAvalableThread();
        startIfPickedMeThread();

        avalableUsers = new ArrayList();
        usersListView = (ListView) findViewById(R.id.lstAvalableUsers);
        userAdapter = new UserListAdapter(this, avalableUsers);
        usersListView.setAdapter(userAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                FragmentManager waitFragmentManager = getFragmentManager();
                pleaseWaitDialog = new FragmentPleaseWait();
                pleaseWaitDialog.setCancelable(false);
                pleaseWaitDialog.show(waitFragmentManager, null);
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
                            byte[] buffer = new byte[32];
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
                        if (result.equals("ok&" + pickedUserName)) {
                            checkAvalableThread.stopThread();
                            pleaseWaitDialog.dismiss();
                            Intent data = new Intent();
                            data.putExtra(MainActivity.USER_NAME, userName);
                            data.putExtra(MainActivity.PASSWORD, password);
                            data.putExtra(MainActivity.USERPICKED, pickedUserName);
                            setResult(RESULT_OK, data);
                            finish();
                        } else {
                            Toast.makeText(UserPickActivity.this, "User havn't Picked. Please try again", Toast.LENGTH_SHORT).show();
                            pleaseWaitDialog.dismiss();

                        }
                    }
                }.execute(userName, password, pickedUserName);
            }
        });
    }

    private void startCheckAvalableThread() {
        checkAvalableThread = new CheckAvalableUsersThread(userName, password);
        checkAvalableThread.setUserListListiner(this);
        checkAvalableThread.start();
    }

    private void stopCheckAvalableThread (){
        checkAvalableThread.interrupt();
        checkAvalableThread.stopThread();
    }

    private void startIfPickedMeThread() {
        ifPickedMeThread = new IfPickedMeThread(userName, password);
        ifPickedMeThread.setListiner(this);
        ifPickedMeThread.start();
    }

    private void stopIfPickedMeThread (){
        ifPickedMeThread.interrupt();
        ifPickedMeThread.stopThread();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCheckAvalableThread();
        stopIfPickedMeThread();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCheckAvalableThread();
        startIfPickedMeThread();
    }

    @Override
    public void OnChangeUserList(List avalableUsers) {

        this.avalableUsers.removeAll(this.avalableUsers);
        for (int i = 0; i < avalableUsers.size(); i++) {
            this.avalableUsers.add((Users) avalableUsers.get(i));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onSomeonePickedMe(String userPicked) {

        FragmentManager youVPickedManager = getFragmentManager();
        pickedDialog = new FragmentSomeonePickedYou();
        pickedDialog.setUserPicked(userPicked);
        pickedDialog.show(youVPickedManager, null);

    }

}