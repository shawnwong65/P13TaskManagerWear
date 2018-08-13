package com.example.a16022774.p13_taskmanagerwear;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<Task> tasks;
    ArrayAdapter<Task> adapter;
    Button btnAdd;
    int actReqCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lv);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        DBHelper dbh = new DBHelper(this);
        tasks = dbh.getAllTasks();
        adapter = new ArrayAdapter<Task>(this, android.R.layout.simple_list_item_1, tasks);
        lv.setAdapter(adapter);

        CharSequence reply = null;
        Intent intent = getIntent();
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null){
            reply = remoteInput.getCharSequence("status");
        }

        if(reply != null){
            Toast.makeText(MainActivity.this, "You have indicated: " + reply,
                    Toast.LENGTH_SHORT).show();

            if(reply.toString().equalsIgnoreCase("Completed")){
                DBHelper db = new DBHelper(MainActivity.this);
                Intent i = getIntent();
                int id = i.getIntExtra("id", 0);
                db.deleteTask(id);
                tasks.clear();
                tasks.addAll(db.getAllTasks());
                db.close();
                adapter.notifyDataSetChanged();
            }
        }

        CharSequence replyAdd = null;
        Intent intentAdd = getIntent();
        Bundle remoteInputAdd = RemoteInput.getResultsFromIntent(intentAdd);
        if (remoteInputAdd != null){
            replyAdd = remoteInputAdd.getCharSequence("add");
        }

        if(replyAdd != null){
            DBHelper db = new DBHelper(MainActivity.this);
            db.insertTask("", replyAdd + "");
            tasks.clear();
            tasks.addAll(db.getAllTasks());
            db.close();
            adapter.notifyDataSetChanged();
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(i, actReqCode);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task selectedItem = tasks.get(position);
                int selectedId = selectedItem.getId();
                DBHelper dbh = new DBHelper(MainActivity.this);
                dbh.deleteTask(selectedId);
                tasks.clear();
                tasks.addAll(dbh.getAllTasks());
                dbh.close();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == actReqCode) {
            if (resultCode == RESULT_OK) {
                DBHelper dbh = new DBHelper(MainActivity.this);
                tasks.clear();
                tasks.addAll(dbh.getAllTasks());
                dbh.close();
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
