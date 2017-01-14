package com.example.avidavidov.ninepuzzle;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends Activity {

    public static final int Empty = 9;
    Button button, tempCell, emptyCell, pushedButton;
    LinearLayout boardLayout;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boardLayout = (LinearLayout) findViewById(R.id.boardLayout);


        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        buttonParams.weight = 1;

        for (int i = 0; i < 3; i++) {

            LinearLayout rowView = new LinearLayout(this);
            rowView.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < 3; j++) {
                position = i * 3 + j + 1;
                button = new Button(this);
                button.setOnClickListener(cellClickListiner);
                button.setTag(position);
                if (position != 9)
                    button.setText(String.valueOf(position));

                rowView.addView(button, buttonParams);
            }

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            boardLayout.addView(rowView, rowParams);
        }

//        boardLayout.findViewWithTag(1).setBackgroundResource(R.drawable.one);
//        boardLayout.findViewWithTag(2).setBackgroundResource(R.drawable.tow);
//        boardLayout.findViewWithTag(3).setBackgroundResource(R.drawable.three);
//        boardLayout.findViewWithTag(4).setBackgroundResource(R.drawable.four);
//        boardLayout.findViewWithTag(5).setBackgroundResource(R.drawable.five);
//        boardLayout.findViewWithTag(6).setBackgroundResource(R.drawable.six);
//        boardLayout.findViewWithTag(7).setBackgroundResource(R.drawable.seven);
//        boardLayout.findViewWithTag(8).setBackgroundResource(R.drawable.eight);


    }

    private View.OnClickListener cellClickListiner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, v.getTag() + " pushed", Toast.LENGTH_SHORT).show();

            pushedButton = (Button) v.findViewWithTag(v.getTag());
            emptyCell = (Button) button.findViewWithTag(Empty);


            emptyCell.setText(v.getTag().toString());
            emptyCell.setTag(v.getTag());

            pushedButton.setText("");
            tempCell = pushedButton;
            pushedButton.setTag(Empty);

//            emptyCell = (Button) button.findViewWithTag(Empty);
//            emptyCell.setText(v.getTag().toString());
//            Button tempCell = (Button) v;
//            v=emptyCell;
//            Button pushedButton = (Button) v.findViewWithTag(v.getTag());
//            pushedButton.setText("");
//            pushedButton.setTag(Empty);


        }
    };


//    public void newGame() {
//
//        Random random = new Random(1 - 9);
//        for (int i = 0; i < 10; i++) {
//            randomButton.findViewWithTag(random.nextInt());
//            randomButton.setOnClickListener(cellClickListiner);
//        }
//    }

}



