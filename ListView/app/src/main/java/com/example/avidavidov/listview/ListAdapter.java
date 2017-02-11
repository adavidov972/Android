package com.example.avidavidov.listview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by avi.davidov on 10/02/2017.
 */

public class ListAdapter extends ArrayAdapter {

    Activity activity;
    List list;

    public ListAdapter (Activity activity, List list) {
        super(activity,R.layout.list_item,list);
        this.activity = activity;
        this.list = list;
    }

    private class ViewContainer {
        TextView lblUserName,lblPassword;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewContainer viewContainer;
        View rowView = convertView;

        if (rowView == null){

            LayoutInflater inflater = activity.getLayoutInflater();
            inflater.inflate(R.layout.list_item,null);

            viewContainer = new ViewContainer();
            viewContainer.lblUserName = (TextView) rowView.findViewById(R.id.lblUserName);
            //viewContainer.lblPassword = (TextView) rowView.findViewById(R.id.lblpassword);

            rowView.setTag(viewContainer);
        }
        else {
            viewContainer = (ViewContainer) rowView.getTag();
        }


        viewContainer.lblUserName.setText(list.get(position).toString());
        return rowView;
    }
}
