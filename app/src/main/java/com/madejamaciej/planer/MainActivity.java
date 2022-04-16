package com.madejamaciej.planer;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    int mYear;
    int mMonth;
    int mDay;
    Activity that;
    File file;
    RelativeLayout relativeCheckboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        that = this;
        setContentView(R.layout.activity_main);
        CalendarView calendarView = (CalendarView) findViewById(R.id.mainCalendar);
        relativeCheckboxes = (RelativeLayout) findViewById(R.id.relativeCheckboxes);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendarView.getDate());
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        file = new File(getApplicationContext().getFilesDir(),"tasks.json");
        if(!file.exists()){
            CreateEmptyFile("tasks.json");
        }
        try{
            Tasks.DisplayDateInfo(mYear, mMonth, mDay, relativeCheckboxes, getApplicationContext(), that);
        }catch(JSONException e){}
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                view.setDate(calendar.getTimeInMillis());

                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;
                try{
                    Tasks.DisplayDateInfo(mYear, mMonth, mDay, relativeCheckboxes, getApplicationContext(), that);
                }catch(JSONException e){}
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try{
            Tasks.DisplayDateInfo(mYear, mMonth, mDay, relativeCheckboxes, getApplicationContext(), that);
        }catch(JSONException e){}
    }

    private void CreateEmptyFile(String filename) {
        try{
            File f = new File(getApplicationContext().getFilesDir(), filename);
            f.createNewFile();
            FileWriter fileWriter = new FileWriter(f);
            JSONObject startingJson = new JSONObject();
            fileWriter.write(startingJson.toString());
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception e){ }
    }

    public void AddTask(View view) {
        Intent myIntent = new Intent(this, AddTaskToDay.class);
        myIntent.putExtra("day", mDay);
        myIntent.putExtra("month", mMonth);
        myIntent.putExtra("year", mYear);
        this.startActivity(myIntent);
    }
}