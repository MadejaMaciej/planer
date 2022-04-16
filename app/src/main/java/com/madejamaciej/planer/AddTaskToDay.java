package com.madejamaciej.planer;

import com.madejamaciej.planer.utils.Tasks;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import org.json.JSONException;
import java.io.File;
import java.io.IOException;

public class AddTaskToDay extends AppCompatActivity {
    int mYear;
    int mMonth;
    int mDay;
    EditText edit;
    File file;
    Activity that;
    RelativeLayout relativeCheckboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        that = this;
        setContentView(R.layout.activity_add_task_to_day);
        Intent intent = getIntent();
        mYear = intent.getIntExtra("year", 2022);
        mMonth = intent.getIntExtra("month", 0);
        mDay = intent.getIntExtra("day", 20);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edit = (EditText) findViewById(R.id.plain_text_input);
        file = new File(getApplicationContext().getFilesDir(),"tasks.json");
        relativeCheckboxes = (RelativeLayout) findViewById(R.id.relativeCheckboxes);
        try {
            Tasks.DisplayDateInfo(mYear, mMonth, mDay, relativeCheckboxes, getApplicationContext(), that);
        } catch (JSONException e) { }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void TaskDayAddition(View view) {
        String newTaskName = edit.getText().toString();
        if(!newTaskName.equals("")){
            try{
                Tasks.AddTasks(newTaskName, "tasks.json", mDay, mMonth, mYear, this, relativeCheckboxes, that);
            } catch(IOException e){}
        }

        edit.setText("");
    }
}