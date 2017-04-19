package com.example.avidavidov.tictactoe_clientside;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Game_Activity extends Activity implements GameStatusThread.OnChangeGameStatusListiner {

    LinearLayout boardLayout;
    TicTacToe game;
    TextView lblUserPicked, lblGameResult;
    String userName, password;
    String xPlayer;
    String crclPlayer;
    int cellPlayed;
    ImageView cellView;
    int gameNumber;
    private int moveCounter;
    boolean isClickable = false;
    Button btnNewGame;
    View parent;
    GameStatusThread statusThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        btnNewGame = (Button) findViewById(R.id.btnNewGame);
        btnNewGame.setVisibility(View.INVISIBLE);

        lblGameResult = (TextView) findViewById(R.id.lblGameResult);
        lblGameResult.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        userName = intent.getStringExtra(MainActivity.USER_NAME);
        password = intent.getStringExtra(MainActivity.PASSWORD);
        xPlayer = intent.getStringExtra(MainActivity.X_PLAYER);
        crclPlayer = intent.getStringExtra(MainActivity.CRCL_PLAYER);
        gameNumber = intent.getIntExtra(MainActivity.GAMENUMBER, -1);
        lblUserPicked = (TextView) findViewById(R.id.lblUserPicked);

        if (userName.equals(xPlayer)) {
            lblUserPicked.setText(crclPlayer);
            isClickable = true;
        } else {
            lblUserPicked.setText(xPlayer);
        }

        game = new TicTacToe();
        moveCounter = 0;
        boardLayout = (LinearLayout) findViewById(R.id.boardLayout);
        int width = boardLayout.getLayoutParams().width;
        int margin = 5;
        int imageSize = (width / 3 - margin * 2);
        for (int i = 0; i < 3; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < 3; j++) {
                cellView = new ImageView(this);
                cellView.setOnClickListener(cellClickListener);
                LinearLayout.LayoutParams cellViewLayout =
                        new LinearLayout.LayoutParams(imageSize, imageSize);
                cellViewLayout.setMargins(margin, margin, margin, margin);
                cellView.setTag(i * 3 + j + 1);

                row.addView(cellView, cellViewLayout);
            }
            LinearLayout.LayoutParams rowLayout = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );

            boardLayout.addView(row, rowLayout);
        }
        parent = (View) cellView.getParent().getParent();
        startGameStatusThread();
    }

    private View.OnClickListener cellClickListener = new View.OnClickListener() {
        @Override

        public void onClick(View v) {

            if (isClickable) {

                cellPlayed = (Integer) v.getTag();
                moveCounter++;
                if (statusThread != null)
                    statusThread.setMoveCounter(moveCounter);
                new AsyncTask<Integer, Void, String>() {

                    @Override
                    protected String doInBackground(Integer... params) {

                        int gameNumber = params[0];
                        int cellPlayed = params[1];
                        String result = "";
                        InputStream inputStream = null;
                        OutputStream outputStream = null;
                        HttpURLConnection connection = null;
                        JSONObject moveRequest = new JSONObject();
                        try {
                            moveRequest.put("gameNumber", gameNumber);
                            moveRequest.put("xPlayer", xPlayer);
                            moveRequest.put("crclPlayer", crclPlayer);
                            moveRequest.put("cellPlayed", cellPlayed);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            URL url = new URL(MainActivity.BASE_URL);
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setUseCaches(false);
                            connection.setDoOutput(true);
                            connection.connect();
                            outputStream = connection.getOutputStream();
                            outputStream.write(moveRequest.toString().getBytes());
                            inputStream = connection.getInputStream();
                            byte[] buffer = new byte[256];
                            int actuallyRead;
                            while ((actuallyRead = inputStream.read(buffer)) != -1) {
                                result = new String(buffer, 0, actuallyRead);
                            }
                            return result;

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
                            if (outputStream != null)
                                try {
                                    outputStream.close();
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

                        isClickable = false;

                        switch (result) {

                            case "you win":
                                stopGameStatusThread();
                                lblGameResult.setVisibility(View.VISIBLE);
                                lblGameResult.setText("ניצחת, כל הכבוד !!!");

                                btnNewGame.setVisibility(View.VISIBLE);
                                btnNewGame.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Game_Activity.this, UserPickActivity.class);
                                        intent.putExtra(MainActivity.USER_NAME, userName);
                                        intent.putExtra(MainActivity.PASSWORD, password);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                break;

                            case "next move":

                                break;

                            case "draw":
                                stopGameStatusThread();
                                lblGameResult.setVisibility(View.VISIBLE);
                                lblGameResult.setText("תיקו !!!");
                                btnNewGame.setVisibility(View.VISIBLE);
                                btnNewGame.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                });
                                break;

                            default:
                                break;
                        }
                    }
                }.execute(gameNumber, cellPlayed);

                if (userName.equals(xPlayer)) {
                    v.setBackground(getResources().getDrawable(R.drawable.x));
                } else {
                    v.setBackground(getResources().getDrawable(R.drawable.o));
                    // TODO: 07/04/2017 switch the deprikated getDrowable to new one
                }
                v.setClickable(false);
            }
        }
    };

    private void startGameStatusThread() {
        statusThread = new GameStatusThread(userName, password,gameNumber);
        statusThread.setListiner(Game_Activity.this);
        statusThread.setMoveCounter(moveCounter);
        statusThread.start();
    }

    private void stopGameStatusThread() {

        if (statusThread != null) {
            statusThread.stopThread();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent();
        intent.putExtra(MainActivity.USER_NAME, userName);
        intent.putExtra(MainActivity.PASSWORD, password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGameStatusThread();
        // TODO: 05/04/2017 tell to the server that activity destroied and game over
    }

    @Override
    public void onChangeGameStatus(int cellPlayed, boolean isXTurn) {

        moveCounter++;
        statusThread.setMoveCounter(moveCounter);
        makeMoveOnScreen(cellPlayed, isXTurn);
        isClickable = true;
    }

    private void makeMoveOnScreen(final int cellPlayed, final boolean isXTurn) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView cell = (ImageView) parent.findViewWithTag(cellPlayed);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (isXTurn) {
                        cell.setBackground(getResources().getDrawable(R.drawable.x, null));
                    } else {
                        cell.setBackground(getResources().getDrawable(R.drawable.o));
                    }
                }
            }
        });
    }

    @Override
    public void onNoComment() {
        Intent intent = new Intent(this, UserPickActivity.class);
        intent.putExtra(MainActivity.USER_NAME, userName);
        intent.putExtra(MainActivity.PASSWORD, password);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDraw(int cellPlayed,boolean isXTurn) {

        makeMoveOnScreen(cellPlayed,isXTurn);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lblGameResult.setVisibility(View.VISIBLE);
                lblGameResult.setText("תיקו !!!");
                btnNewGame.setVisibility(View.VISIBLE);
            }
        });
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onLose(int cellPlayed, boolean isXTurn) {

        makeMoveOnScreen(cellPlayed,isXTurn);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lblGameResult.setVisibility(View.VISIBLE);
                lblGameResult.setText("הפסדת !!!, נסה שנית");
                btnNewGame.setVisibility(View.VISIBLE);
            }
        });

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // TODO: 07/04/2017 put on the screen "you lose" massege...
    }
}