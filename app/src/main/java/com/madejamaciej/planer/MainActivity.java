package com.madejamaciej.planer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    int mYear;
    int mMonth;
    int mDay;
    File file;
    String tasks;
    JSONObject dayTasks;
    RelativeLayout relativeCheckboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            DisplayDateInfo(mYear, mMonth, mDay);
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
                    DisplayDateInfo(mYear, mMonth, mDay);
                }catch(JSONException e){}
            }
        });
    }

    public void DisplayDateInfo(int year, int month, int day) throws JSONException {
        dayTasks = LoadTasksForDay(year, month, day);
        relativeCheckboxes.removeAllViews();
        if(dayTasks != null){
            int iterator = 0;
            Iterator<String> keys = dayTasks.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                boolean value = (boolean) dayTasks.get(key);
                CheckBox cb = new CheckBox(getApplicationContext());
                cb.setText(key);
                cb.setChecked(value);
                cb.setId(getRandomNumber(10000000, 999999999));
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) findViewById(v.getId());
                        if(cb != null){
                            cb.setClickable(false);
                            String text = (String) cb.getText();
                            try {
                                finishTask(text, "tasks.json");
                            } catch (IOException e) { }
                        }
                    }
                });
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = 100*iterator;
                if(value){
                    cb.setClickable(false);
                }
                relativeCheckboxes.addView(cb, params);
                iterator++;
            }
        }
    }

    public void finishTask(String text, String filename) throws IOException {
        try{
            File f = new File(getApplicationContext().getFilesDir(), filename);
            FileWriter fileWriter = new FileWriter(f);
            JSONObject tasksObject = new JSONObject(tasks);
            tasksObject = tasksObject.getJSONObject("task-"+mDay+"-"+mMonth+"-"+mYear);
            tasksObject.put(text, true);
            JSONObject wholePlanner = new JSONObject(tasks);
            wholePlanner.put("task-"+mDay+"-"+mMonth+"-"+mYear, tasksObject);
            fileWriter.write(wholePlanner.toString());
            fileWriter.flush();
            fileWriter.close();
            tasks = wholePlanner.toString();
        }catch (Exception e) {}
    }

    public JSONObject LoadTasksForDay(int year, int month, int day) {
        tasks = getJsonFromAssets("tasks.json");
        JSONObject tasksObject = null;
        try{
            tasksObject = new JSONObject(tasks);
            tasksObject = tasksObject.getJSONObject("task-"+day+"-"+month+"-"+year);
        }catch (Exception e) { return null; }
        if(tasksObject != null){
           return tasksObject;
        }
        return null;
    }

    public void CreateEmptyFile(String filename) {
        try{
            File f = new File(getApplicationContext().getFilesDir(), filename);
            f.createNewFile();
            FileWriter fileWriter = new FileWriter(f);
            JSONObject dataObj = new JSONObject();
            JSONObject taskObj = new JSONObject();
            taskObj.put("Task Name", false);
            dataObj.put("task-"+mDay+"-"+mMonth+"-"+mYear, taskObj);
            JSONObject newObj = (JSONObject) dataObj.get("task-"+mDay+"-"+mMonth+"-"+mYear);
            newObj.put("New Task 2", true);
            dataObj.put("task-"+mDay+"-"+mMonth+"-"+mYear, newObj);
            fileWriter.write(dataObj.toString());
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

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }


    public String getJsonFromAssets(String fileName) {
        String jsonString;
        InputStream inputStream = null;
        try {
            File f = new File(getApplicationContext().getFilesDir(), fileName);
            inputStream = new FileInputStream(f);
            jsonString = readFromInputStream(inputStream);
        } catch (Exception e) {
            return null;
        }

        return jsonString;
    }

    public String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}

//putting new task
//JSONObject dataObj = new JSONObject();
//JSONObject taskObj = new JSONObject();
//taskObj.put("Task Name", false);
//dataObj.put("task-"+mDay+"-"+mMonth+"-"+mYear, taskObj);
//JSONObject newObj = (JSONObject) dataObj.get("task-"+mDay+"-"+mMonth+"-"+mYear);
//newObj.put("New Task 2", true);
//dataObj.put("task-"+mDay+"-"+mMonth+"-"+mYear, newObj);
//fileWriter.write(dataObj.toString());