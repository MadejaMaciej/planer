package com.madejamaciej.planer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

public class Tasks {
    public static void DisplayDateInfo(int year, int month, int day, RelativeLayout relativeCheckboxes, Context context, Activity activity) throws JSONException {
        JSONObject dayTasks = LoadTasksForDay(year, month, day, context);
        relativeCheckboxes.removeAllViews();
        if(dayTasks != null){
            int iterator = 0;
            Iterator<String> keys = dayTasks.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                boolean value = (boolean) dayTasks.get(key);
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                relativeCheckboxes.addView(ll);
                CheckBox cb = new CheckBox(context);
                cb.setText(key);
                cb.setChecked(value);
                cb.setId(Random.GetRandomNumber(10000000, 999999999));
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) activity.findViewById(v.getId());

                        if(cb != null){
                            boolean isChecked = cb.isChecked();
                            String text = (String) cb.getText();

                            if(isChecked){
                                try {
                                    FinishTask(text, "tasks.json", context, day, month, year);
                                } catch (IOException e) { }
                            }else{
                                try {
                                    UnfinishTask(text, "tasks.json", context, day, month, year);
                                } catch (IOException e) { }
                            }

                        }
                    }
                });

                Button edit = new Button(context);
                edit.setText("Edit");
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = 100*iterator;
                ll.addView(cb, params);
                LinearLayout.LayoutParams paramsBtn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsBtn.topMargin = 100*iterator;
//                ll.addView(edit, paramsBtn);
                iterator++;
            }
        }
    }

    public static void AddTasks(String task, String filename, int d, int m, int y, Context context, RelativeLayout rc, Activity th) throws IOException {
        try{
            String tasks = GetJsonFromAssets(filename, context);
            File f = new File(context.getFilesDir(), filename);
            FileWriter fileWriter = new FileWriter(f);
            JSONObject tasksObject;
            JSONObject wholePlanner;
            if(tasks != null){
                tasksObject = new JSONObject(tasks);
            }else{
                tasksObject = new JSONObject();
            }
            if(tasksObject.has("task-"+d+"-"+m+"-"+y)){
                tasksObject = tasksObject.getJSONObject("task-"+d+"-"+m+"-"+y);
                tasksObject.put(task, false);
                if(tasks != null){
                    wholePlanner = new JSONObject(tasks);
                }else {
                    wholePlanner = new JSONObject();
                }
                wholePlanner.put("task-"+d+"-"+m+"-"+y, tasksObject);
            }else{
                tasksObject = new JSONObject();
                tasksObject.put(task, false);
                if(tasks != null){
                    wholePlanner = new JSONObject(tasks);
                }else {
                    wholePlanner = new JSONObject();
                }
                wholePlanner.put("task-"+d+"-"+m+"-"+y, tasksObject);
            }
            fileWriter.write(wholePlanner.toString());
            fileWriter.flush();
            fileWriter.close();

        }catch(JSONException e){
            Log.e("error", e.toString());
        }

        try {
            DisplayDateInfo(y, m, d, rc, context, th);
        } catch (JSONException e) { }
    }

    private static void FinishTask(String text, String filename, Context context, int d, int m, int y) throws IOException {
        try{
            String tasks = GetJsonFromAssets("tasks.json", context);
            File f = new File(context.getFilesDir(), filename);
            FileWriter fileWriter = new FileWriter(f);
            JSONObject tasksObject = new JSONObject(tasks);
            tasksObject = tasksObject.getJSONObject("task-"+d+"-"+m+"-"+y);
            tasksObject.put(text, true);
            JSONObject wholePlanner = new JSONObject(tasks);
            wholePlanner.put("task-"+d+"-"+m+"-"+y, tasksObject);
            fileWriter.write(wholePlanner.toString());
            fileWriter.flush();
            fileWriter.close();
            tasks = wholePlanner.toString();
        }catch (Exception e) {
        }
    }

    private static void UnfinishTask(String text, String filename, Context context, int d, int m, int y) throws IOException {
        try{
            String tasks = GetJsonFromAssets("tasks.json", context);
            File f = new File(context.getFilesDir(), filename);
            FileWriter fileWriter = new FileWriter(f);
            JSONObject tasksObject = new JSONObject(tasks);
            tasksObject = tasksObject.getJSONObject("task-"+d+"-"+m+"-"+y);
            tasksObject.put(text, false);
            JSONObject wholePlanner = new JSONObject(tasks);
            wholePlanner.put("task-"+d+"-"+m+"-"+y, tasksObject);
            fileWriter.write(wholePlanner.toString());
            fileWriter.flush();
            fileWriter.close();
            tasks = wholePlanner.toString();
        }catch (Exception e) {
        }
    }

    private static JSONObject LoadTasksForDay(int year, int month, int day, Context context) {
        String tasks = GetJsonFromAssets("tasks.json", context);
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

    private static String GetJsonFromAssets(String fileName, Context context) {
        String jsonString;
        InputStream inputStream = null;
        try {
            File f = new File(context.getFilesDir(), fileName);
            inputStream = new FileInputStream(f);
            jsonString = ReadFromInputStream(inputStream);
        } catch (Exception e) {
            return null;
        }

        return jsonString;
    }

    private static String ReadFromInputStream(InputStream inputStream) throws IOException {
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
