package com.example.avidavidov.tictactoe_clientside;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by avi.davidov on 10/01/2017.
 */

public class UserListAdapter extends ArrayAdapter implements OnChangeUserListListiner {
    private Activity activity;
    private List <Users> users;
    CheckAvalableUsersThread thread;


    public UserListAdapter(Activity activity, List <Users> users) {
        super(activity, R.layout.user_item,users);
        this.activity = activity;
        this.users = users;
    }

    static class ViewContainer {

        ImageView userImage;
        TextView lbluserName;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        thread = new CheckAvalableUsersThread();
        thread.setUserListListiner(UserListAdapter.this);
        View rowView = convertView;
        ViewContainer viewContainer = null;
        if (rowView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.user_item, null);
            viewContainer = new ViewContainer();
            viewContainer.userImage = (ImageView) rowView.findViewById(R.id.imgUserIcon);
            viewContainer.lbluserName = (TextView) rowView.findViewById(R.id.lblUsername);
            rowView.setTag(viewContainer);
        }else {
            viewContainer = (ViewContainer) rowView.getTag();
        }

        viewContainer.lbluserName.setText(users.get(position).getUserName().toString());
        return rowView;
    }

    @Override
    public void OnChangeUserList(List <Users> avalableUsers) {
        this.users = avalableUsers;
        notifyDataSetChanged();
    }

}
