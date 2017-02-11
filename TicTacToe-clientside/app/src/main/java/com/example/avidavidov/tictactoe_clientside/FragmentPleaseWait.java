package com.example.avidavidov.tictactoe_clientside;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by avi.davidov on 11/02/2017.
 */

public class FragmentPleaseWait extends DialogFragment {

TextView lblWait1,lblWait2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wait, container);
        lblWait1 = (TextView) view.findViewById(R.id.lblWait1);
        lblWait2 = (TextView) view.findViewById(R.id.lblWait2);

        return view;

    }
}
