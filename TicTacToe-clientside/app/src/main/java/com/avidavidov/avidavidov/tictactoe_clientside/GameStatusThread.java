package com.avidavidov.avidavidov.tictactoe_clientside;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by avi.davidov on 25/02/2017.
 */

public class GameStatusThread extends Thread {

    String userName;
    String password;
    int gameNumber;
    private int moveCounter = 0;
    private OnChangeGameStatusListiner listiner;
    Map<String, String> qs;
    private boolean go = true;
    private int waitToMoveCounter = 0;


    public GameStatusThread(String userName, String password, int gameNumber) {
        this.userName = userName;
        this.password = password;
        this.gameNumber = gameNumber;
    }

    public void setMoveCounter(int moveCounter) {
        this.moveCounter = moveCounter;
    }

    public void setListiner(OnChangeGameStatusListiner listiner) {
        this.listiner = listiner;
    }

    @Override
    public void run() {

        while (go) {

            HttpURLConnection connection = null;
            InputStream inputStream = null;

            try {
                URL url = new URL(MainActivity.BASE_URL + "?action=gameStatus" + "&userName=" + userName + "&password=" + password
                        + "&gameNumber=" + gameNumber + "&moveCounter=" + moveCounter);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                connection.connect();
                inputStream = connection.getInputStream();
                int actuallyRead;
                byte[] buffer = new byte[64];
                String result = "";
                while ((actuallyRead = inputStream.read(buffer)) != -1)
                    result = new String(buffer, 0, actuallyRead);
                inputStream.close();
                connection.disconnect();

                if (result != null)
                    getQuery(result);

                String action = qs.get("action");
                if (action != null) {

                    int cellPlayed;
                    String turn;
                    boolean isXTurn = false;

                    switch (action) {

                        case "move":

                            cellPlayed = Integer.valueOf(qs.get("cellPlayed"));
                            turn = qs.get("turn");
                            if (turn.equals("X"))
                                isXTurn = true;
                            if (listiner != null)
                                listiner.onChangeGameStatus(cellPlayed, isXTurn);
                            break;

                        case "no change":

                            waitToMoveCounter++;
                            if (waitToMoveCounter == 60) {

                                if (listiner != null)
                                    listiner.onNoComment();
                            }
                            break;

                        case "draw":

                            stopThread();
                            cellPlayed = Integer.valueOf(qs.get("cellPlayed"));
                            turn = qs.get("turn");
                            if (turn.equals("X"))
                                isXTurn = true;
                            if (listiner != null)
                                listiner.onDraw(cellPlayed,isXTurn);
                            break;

                        case "youlost":

                            cellPlayed = Integer.valueOf(qs.get("cellPlayed"));
                            turn = qs.get("turn");
                            if (turn.equals("X"))
                                isXTurn = true;
                            stopThread();
                            if (listiner != null)
                                listiner.onLose(cellPlayed,isXTurn);
                            break;
                    }
                }

            } catch (MalformedURLException e)

            {
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
                if (connection != null)
                    connection.disconnect();
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
        }
    }

    public void getQuery(String query) {

        if (query == null || query.isEmpty())
            return;
        String[] keysAndValues = query.split("&");
        qs = new HashMap<>();

        for (int i = 0; i < keysAndValues.length; i++) {
            String[] keyAndValue = keysAndValues[i].split("=");
            if (keyAndValue.length != 2)
                continue;
            qs.put(keyAndValue[0], (keyAndValue[1]));

        }
    }

    public void stopThread() {
        interrupt();
        go = false;
    }

    interface OnChangeGameStatusListiner {
        void onChangeGameStatus(int cellPlayed, boolean isXTurn);

        void onNoComment();

        void onDraw(int cellPlayed, boolean isXTurn);

        void onLose(int cellPlayed, boolean isXTurn);
    }
}


