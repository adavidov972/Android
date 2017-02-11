package com.example.avidavidov.tictactoe_clientside;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Game_Activity extends AppCompatActivity {

    LinearLayout boardLayout;
    TicTacToe game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


            game = new TicTacToe();
            boardLayout = (LinearLayout)findViewById(R.id.boardLayout);
            int width = boardLayout.getLayoutParams().width;
            int margin = 5;
            int imageSize = (width/3- margin*2);
            for (int i = 0; i < 3; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j < 3; j++) {
                    ImageView cellView = new ImageView(this);
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
                int cell = (Integer)v.getTag();
                if (game.isXturn() == true) {
                    v.setBackground(getResources().getDrawable(R.drawable.x));
                }
                else {
                    v.setBackground(getResources().getDrawable(R.drawable.o));

                }
                v.setClickable(false);
                game.makeMove(cell);

            }
        };

    }

