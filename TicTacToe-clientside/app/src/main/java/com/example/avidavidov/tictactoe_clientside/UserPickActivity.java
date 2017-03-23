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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class UserPickActivity extends Activity implements CheckAvalableUsersThread.OnChangeUserListListiner, IfPickedMeThread.OnPickingMeListiner,
        FragmentSomeonePickedYou.OnUserApprovedListiner {
    private ListView usersListView;
    private UserListAdapter userAdapter;
    private List<Users> avalableUsers;
    private String userName, password, pickingResult;
    private String userPicked;
    private CheckAvalableUsersThread checkAvalableThread;
    private IfPickedMeThread ifPickedMeThread;
    private FragmentPleaseWait pleaseWaitDialog;
    private FragmentSomeonePickedYou pickedDialog;
    TextView lblConnectedAs;
    private Button btnSwitchUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pick);
        lblConnectedAs = (TextView) findViewById(R.id.lblConnectedAs);
        btnSwitchUser = (Button) findViewById(R.id.btnSwitchUser);

        Intent intent = getIntent();
        if (intent.getStringExtra(MainActivity.USER_NAME) != null)
            userName = intent.getStringExtra(MainActivity.USER_NAME);
        if (intent.getStringExtra(MainActivity.PASSWORD) != null)
            password = intent.getStringExtra(MainActivity.PASSWORD);

        lblConnectedAs.setText("Connected as : " + userName);
        startCheckAvalableThread();
        startIfPickedMeThread();

        avalableUsers = new ArrayList();
        usersListView = (ListView) findViewById(R.id.lstAvalableUsers);
        userAdapter = new UserListAdapter(this, avalableUsers);
        usersListView.setAdapter(userAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stopIfPickedMeThread();
                stopCheckAvalableThread();
                FragmentManager waitFragmentManager = getFragmentManager();
                pleaseWaitDialog = new FragmentPleaseWait();
                pleaseWaitDialog.setCancelable(false);
                pleaseWaitDialog.show(waitFragmentManager, null);
                userPicked = avalableUsers.get(position).getUserName().toString();

                new AsyncTask<String, Void, String>() {

                    InputStream inputStream = null;
                    String resultArray = "";

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
                                resultArray = new String(buffer, 0, actuallyRead);

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return resultArray;
                    }

                    @Override
                    protected void onPostExecute(String resultArray) {
                        String[] resultWithGameNumber = resultArray.split("~");
                        final String result = resultWithGameNumber[0];
                        final int gameNumber = Integer.valueOf(resultWithGameNumber[1]);
                        if (result.equals("ok&" + userPicked)) {
                            checkAvalableThread.stopThread();
                            ifPickedMeThread.stopThread();

                            new AsyncTask<String, Void, String>() {
                                @Override
                                protected String doInBackground(String... params) {

                                    params[0] = userName;
                                    params[1] = userPicked;
                                    params[2] = String.valueOf(gameNumber);


                                              return result;
                                }

                            }.execute(userName, userPicked, String.valueOf(gameNumber));


                            pleaseWaitDialog.dismiss();
                            Intent data = new Intent();
                            data.putExtra(MainActivity.USER_NAME, userName);
                            data.putExtra(MainActivity.PASSWORD, password);
                            data.putExtra(MainActivity.USERPICKED, userPicked);
                            data.putExtra(MainActivity.GAMENUMBER, gameNumber);
                            setResult(RESULT_OK, data);
                            finish();
                        } else {
                            Toast.makeText(UserPickActivity.this, "User havn't Picked. Please try again", Toast.LENGTH_SHORT).show();
                            pleaseWaitDialog.dismiss();

                        }
                    }
                }.execute(userName, password, userPicked);
            }
        });
    }

    private void startCheckAvalableThread() {
        checkAvalableThread = new CheckAvalableUsersThread(userName, password);
        checkAvalableThread.setUserListListiner(this);
        checkAvalableThread.start();
    }

    private void stopCheckAvalableThread() {
        checkAvalableThread.interrupt();
        checkAvalableThread.stopThread();
    }

    private void startIfPickedMeThread() {
        ifPickedMeThread = new IfPickedMeThread(userName, password);
        ifPickedMeThread.setListiner(this);
        ifPickedMeThread.start();
    }

    private void stopIfPickedMeThread() {
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
    public void onSomeonePickedMe(String userPicked, int gameNumber) {

        FragmentManager youVPickedManager = getFragmentManager();
        pickedDialog = new FragmentSomeonePickedYou();
        pickedDialog.setUserName(userName);
        pickedDialog.setPassword(password);
        pickedDialog.setUserPicked(userPicked);
        pickedDialog.setGameNumber(gameNumber);
        pickedDialog.show(youVPickedManager, null);

        if (pickedDialog.isApproved) {

        } else {
            startCheckAvalableThread();
            startIfPickedMeThread();
        }


    }

    @Override
    public void onUserApproved(String userName, String userPicked, int gameNumber) {

        Intent data = new Intent();
        data.putExtra(MainActivity.USER_NAME, userName);
        data.putExtra(MainActivity.PASSWORD, password);
        data.putExtra(MainActivity.USERPICKED, userPicked);
        data.putExtra(MainActivity.GAMENUMBER, gameNumber);
        setResult(RESULT_OK, data);
        finish();
    }
}