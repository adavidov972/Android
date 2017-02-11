package com.example.avidavidov.listview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    ListView listView;
    List <String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userList = new ArrayList();

        userList.add(new String ("Avi"));
        userList.add(new String ("Keren"));
        userList.add(new String ("Eyal"));
        userList.add(new String ("Yuval"));
        userList.add(new String ("Hadar"));
        userList.add(new String ("Elad"));
        userList.add(new String ("Yaaco"));

        ListAdapter adapter = new ListAdapter(this,userList);
        listView = (ListView) findViewById(R.id.lstUsers);
        listView.setAdapter(adapter);


    }
}
