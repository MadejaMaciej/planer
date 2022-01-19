package com.madejamaciej.planer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AddTaskToDay extends AppCompatActivity {
    int mYear;
    int mMonth;
    int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_to_day);
        Intent intent = getIntent();
        mYear = intent.getIntExtra("year", 2022);
        mMonth = intent.getIntExtra("month", 0);
        mDay = intent.getIntExtra("day", 20);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void TaskDayAddition(View view) {

    }
}