package com.madejamaciej.planer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class AddTaskToDay extends AppCompatActivity {
    int mYear;
    int mMonth;
    int mDay;
    EditText edit;
    File file;
    String tasks;
    JSONObject dayTasks;
    RelativeLayout relativeCheckboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            DisplayDateInfo(mYear, mMonth, mDay);
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
                AddTasks(newTaskName, "tasks.json");
            } catch(IOException e){}
        }

        edit.setText("");
    }

    private void AddTasks(String task, String filename) throws IOException {
        try{
            File f = new File(getApplicationContext().getFilesDir(), filename);
            FileWriter fileWriter = new FileWriter(f);
            JSONObject tasksObject;
            JSONObject wholePlanner;
            if(tasks != null){
                tasksObject = new JSONObject(tasks);
            }else{
                tasksObject = new JSONObject();
            }
            if(tasksObject.has("task-"+mDay+"-"+mMonth+"-"+mYear)){
                tasksObject = tasksObject.getJSONObject("task-"+mDay+"-"+mMonth+"-"+mYear);
                tasksObject.put(task, false);
                if(tasks != null){
                    wholePlanner = new JSONObject(tasks);
                }else {
                    wholePlanner = new JSONObject();
                }
                wholePlanner.put("task-"+mDay+"-"+mMonth+"-"+mYear, tasksObject);
            }else{
                tasksObject = new JSONObject();
                tasksObject.put(task, false);
                if(tasks != null){
                    wholePlanner = new JSONObject(tasks);
                }else {
                    wholePlanner = new JSONObject();
                }
                wholePlanner.put("task-"+mDay+"-"+mMonth+"-"+mYear, tasksObject);
            }
            fileWriter.write(wholePlanner.toString());
            fileWriter.flush();
            fileWriter.close();

        }catch(JSONException e){
            Log.e("error", e.toString());
        }

        try {
            DisplayDateInfo(mYear, mMonth, mDay);
        } catch (JSONException e) { }
    }

    private void DisplayDateInfo(int year, int month, int day) throws JSONException {
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

    private void finishTask(String text, String filename) throws IOException {
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

    private JSONObject LoadTasksForDay(int year, int month, int day) {
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

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }


    private String getJsonFromAssets(String fileName) {
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

    private String readFromInputStream(InputStream inputStream)
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