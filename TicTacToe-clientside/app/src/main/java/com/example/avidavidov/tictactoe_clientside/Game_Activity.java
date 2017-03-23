package com.example.avidavidov.tictactoe_clientside;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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

public class Game_Activity extends Activity {

    LinearLayout boardLayout;
    TicTacToe game;
    TextView lblUserPicked;
    String userName, password;
    String xPlayer;
    String crclPlayer;
    int cellPlayed;
    ImageView cellView;
    int gameNumber;
    boolean isClickable = true;
    Button btnNewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        btnNewGame = (Button) findViewById(R.id.btnNewGame);
        btnNewGame.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        userName = intent.getStringExtra(MainActivity.USER_NAME);
        password = intent.getStringExtra(MainActivity.PASSWORD);
        xPlayer = intent.getStringExtra(MainActivity.X_PLAYER);
        crclPlayer = intent.getStringExtra(MainActivity.CRCL_PLAYER);
        gameNumber = intent.getIntExtra(MainActivity.GAMENUMBER, -1);
        lblUserPicked = (TextView) findViewById(R.id.lblUserPicked);

        if (userName.equals(xPlayer)) {
            lblUserPicked.setText(crclPlayer);
        } else {
            lblUserPicked.setText(xPlayer);
        }

        game = new TicTacToe();
        boardLayout = (LinearLayout) findViewById(R.id.boardLayout);
        int width = boardLayout.getLayoutParams().width;
        int margin = 5;
        int imageSize = (width / 3 - margin * 2);
        for (int i = 0; i < 3; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < 3; j++) {
                cellView = new ImageView(this);
                if (isClickable)
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

    }

    private View.OnClickListener cellClickListener = new View.OnClickListener() {
        @Override

        public void onClick(View v) {

            cellPlayed = (Integer) v.getTag();
            isClickable=false;
            new AsyncTask<Integer, Void, String>() {

                @Override
                protected String doInBackground(Integer... params) {

                    int gameNumber = params[0];
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

                    if (result.equals("You win")) {
                        lblUserPicked.setText("You win");
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
                        // TODO: 20/03/2017 place "You win" on screen and close game and start thread for next game
                        // TODO: 20/03/2017 make user avalable.
                    }

                    if (result.equals("next move")) {
                        // TODO: 20/03/2017 Thread for game status
                        isClickable = true;
                    }

                }
            }.execute(gameNumber);

            int cell = (Integer) v.getTag();
            if (game.isXturn() == true) {
                v.setBackground(getResources().getDrawable(R.drawable.x));
            } else {
                v.setBackground(getResources().getDrawable(R.drawable.o));

            }
            v.setClickable(false);
            game.makeMove(cell);

        }


    };
}


