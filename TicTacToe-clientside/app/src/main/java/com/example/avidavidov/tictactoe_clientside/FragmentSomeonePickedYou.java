package com.example.avidavidov.tictactoe_clientside;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by avi.davidov on 25/02/2017.
 */

public class FragmentSomeonePickedYou extends DialogFragment {

    TextView pickedYou;
    Button btnApprove, btnDismiss;
    String userPicked = "";

    public void setUserPicked(String userPicked) {
        this.userPicked = userPicked;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_someone_picked_you, container);
        pickedYou = (TextView) view.findViewById(R.id.lblPickedYou);
        pickedYou.setText(userPicked + " wants to play with you");

        btnApprove = (Button) view.findViewById(R.id.btnApprove);
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDismiss = (Button) view.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }






}
