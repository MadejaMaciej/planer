package com.madejamaciej.planer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    int mYear;
    int mMonth;
    int mDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalendarView calendarView = (CalendarView) findViewById(R.id.mainCalendar);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendarView.getDate());
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        displayDateInfo(mYear, mMonth, mDay);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                view.setDate(calendar.getTimeInMillis());

                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;

                displayDateInfo(mYear, mMonth, mDay);
            }
        });
    }

    private void displayDateInfo(int year, int month, int day){
        Log.d("Date changed: ", day+" "+month+" "+ year);
    }

    private void LoadAllTasks() {

    }

    public void addTask(View view) {
        Log.d("Adding task:", "Changing View");
    }
}