package com.example.avidavidov.tictactoe_clientside;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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


public class UserPickActivity extends Activity implements CheckUserStatusThread.OnChangeUsersStatusListiner,
        FragmentSomeonePickedYou.OnUserApprovedListiner, com.example.avidavidov.tictactoe_clientside.ifCrclApprovedThread.OnCrclApprovedListiner {
    private ListView usersListView;
    private UserListAdapter userAdapter;
    private List<Users> avalableUsers;
    private String userName, password;
    private String userPicked;
    private CheckUserStatusThread checkUserStatusThread;
    private ifCrclApprovedThread ifCrclApprovedThread;
    private FragmentPleaseWait pleaseWaitDialog;
    private FragmentSomeonePickedYou pickedDialog;
    TextView lblConnectedAs, lblNoNetwork;
    private Button btnSwitchUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pick);

        lblConnectedAs = (TextView) findViewById(R.id.lblConnectedAs);
        lblNoNetwork = (TextView) findViewById(R.id.lblNoNetwork);
        lblNoNetwork.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        if (intent.getStringExtra(MainActivity.USER_NAME) != null)
            userName = intent.getStringExtra(MainActivity.USER_NAME);
        if (intent.getStringExtra(MainActivity.PASSWORD) != null)
            password = intent.getStringExtra(MainActivity.PASSWORD);
        lblConnectedAs.setText("Connected as : " + userName);

        avalableUsers = new ArrayList();
        usersListView = (ListView) findViewById(R.id.lstAvalableUsers);
        userAdapter = new UserListAdapter(this, avalableUsers);
        usersListView.setAdapter(userAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                stopCheckUserStatusThread();
                FragmentManager waitFragmentManager = getFragmentManager();
                pleaseWaitDialog = new FragmentPleaseWait();
                pleaseWaitDialog.setCancelable(false);
                pleaseWaitDialog.show(waitFragmentManager, null);

                userPicked = avalableUsers.get(position).getUserName();

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

                        if (resultArray != null) {

                            String[] resultWithGameNumber = resultArray.split("~");
                            String result = resultWithGameNumber[0];
                            int gameNumber = Integer.valueOf(resultWithGameNumber[1]);
                            if (result.equals("ok&" + userPicked)) {
                                stopCheckUserStatusThread();
                                startIfCrclApprovedThread(userName, password, gameNumber);


                            } else {
                                Toast.makeText(UserPickActivity.this, "User havn't Picked. Please try again", Toast.LENGTH_SHORT).show();
                                pleaseWaitDialog.dismiss();
                            }
                        }
                    }
                }.execute(userName, password, userPicked);
            }
        });
    }

    private void startIfCrclApprovedThread(String userName, String password, int gameNumber) {
        ifCrclApprovedThread = new ifCrclApprovedThread(userName, password, gameNumber);
        ifCrclApprovedThread.setListiner(this);
        ifCrclApprovedThread.start();
    }

    private void stopIfCrclApprovedThread() {
        ifCrclApprovedThread.interrupt();
        ifCrclApprovedThread.stopThread();
    }

    private void startCheckUserStatusThread() {
        checkUserStatusThread = new CheckUserStatusThread(this,userName, password);
        checkUserStatusThread.setListiner(this);
        checkUserStatusThread.start();
    }

    private void stopCheckUserStatusThread() {
        checkUserStatusThread.stopThread();
        checkUserStatusThread.interrupt();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCheckUserStatusThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCheckUserStatusThread();
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra(MainActivity.USER_NAME, userName);
        intent.putExtra(MainActivity.PASSWORD, password);
        startActivity(intent);
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
        pickedDialog.setListiner(this);
        pickedDialog.show(youVPickedManager, null);
    }

    @Override
    public void onUserApproved(String userName, String userPicked, int gameNumber) {

        Intent data = new Intent(this, Game_Activity.class);
        data.putExtra(MainActivity.USER_NAME, userName);
        data.putExtra(MainActivity.PASSWORD, password);
        data.putExtra(MainActivity.X_PLAYER, userPicked);
        data.putExtra(MainActivity.CRCL_PLAYER, userName);
        data.putExtra(MainActivity.GAMENUMBER, gameNumber);
        stopCheckUserStatusThread();
        startActivity(data);
    }

    @Override
    public void onUserDismissed() {
        startCheckUserStatusThread();
    }

    @Override
    public void onCrclApproved(int gameNumber) {

        pleaseWaitDialog.dismiss();
        stopIfCrclApprovedThread();
        Intent data = new Intent(this, Game_Activity.class);
        data.putExtra(MainActivity.USER_NAME, userName);
        data.putExtra(MainActivity.PASSWORD, password);
        data.putExtra(MainActivity.X_PLAYER, userName);
        data.putExtra(MainActivity.CRCL_PLAYER, userPicked);
        data.putExtra(MainActivity.GAMENUMBER, gameNumber);
        startActivity(data);
    }

    @Override
    public void onCrclDismissedOrNotAnswering() {
        pleaseWaitDialog.dismiss();
    }
}