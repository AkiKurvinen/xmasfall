package com.augergames.xmasfall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class UserActivity extends AppCompatActivity {
    private int userID = 0;
    private String userName = "Guest";
    private int userHiscore = 0;
    private int userLVL = 0;
    private int userXP = 0;
    private String key = "";
    private String userURL = "https://augergames.com/xmasfall/autologin.php";
    private RequestQueue queue;
    private String apikey = "a7e15036691751d80a4ac8d4f005b4b7";
    private boolean needSync = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Button buttonAll = findViewById(R.id.buttonAll);

        TextView usernameTextView = findViewById(R.id.usernameTextView);
        TextView lvlTextView = findViewById(R.id.lvlTextView);
        TextView hiscoreTextView = findViewById(R.id.hiscoreTextView);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();



        if (extras != null) {
            userID = intent.getIntExtra("userID", 0);
            userName = intent.getStringExtra("userName");
            userLVL =  intent.getIntExtra("userLVL", 0);
            userXP = intent.getIntExtra("userXP", 0);
            key = intent.getStringExtra("key");
            userHiscore = intent.getIntExtra("hiscore",0);
            userURL= userURL+"?key="+key;

            usernameTextView.setText(userName);
            lvlTextView.setText("LVL "+userLVL);
            hiscoreTextView.setText("Best score: "+userHiscore);
        }
        queue = Volley.newRequestQueue(this);


        buttonAll.performClick();



    }

    @Override
    public void onResume( ){
        super.onResume();

        if(needSync){
            syncUserData();
        }


        Toast.makeText(this,"onRestoreInstanceState",Toast.LENGTH_SHORT).show();
    }
    public void buttonHandler(int btn){
        Button buttonAll = findViewById(R.id.buttonAll);
        Button buttonYear = findViewById(R.id.buttonYear);
        Button buttonMonth = findViewById(R.id.buttonMonth);
        Button buttonWeek = findViewById(R.id.buttonWeek);
        Button buttonDay = findViewById(R.id.buttonDay);

        if(btn==0){
            buttonAll.setEnabled(false);
            buttonYear.setEnabled(true);
            buttonMonth.setEnabled(true);
            buttonWeek.setEnabled(true);
            buttonDay.setEnabled(true);
        }
        else if(btn==1){
            buttonAll.setEnabled(true);
            buttonYear.setEnabled(false);
            buttonMonth.setEnabled(true);
            buttonWeek.setEnabled(true);
            buttonDay.setEnabled(true);
        }
        else if(btn==2){
            buttonAll.setEnabled(true);
            buttonYear.setEnabled(true);
            buttonMonth.setEnabled(false);
            buttonWeek.setEnabled(true);
            buttonDay.setEnabled(true);
        }
        else if(btn==3){
            buttonAll.setEnabled(true);
            buttonYear.setEnabled(true);
            buttonMonth.setEnabled(true);
            buttonWeek.setEnabled(false);
            buttonDay.setEnabled(true);
        }
        else if(btn==4){
            buttonAll.setEnabled(true);
            buttonYear.setEnabled(true);
            buttonMonth.setEnabled(true);
            buttonWeek.setEnabled(true);
            buttonDay.setEnabled(false);
        }



    }
    public void gotoGame(View view){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userURL)));
        needSync = true;
    }
    public void syncUserData() {
        needSync = false;

        String postUrl ="https://augergames.com/xmasfall/api.php";

        StringRequest sr = new StringRequest(Request.Method.POST,postUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                parseJSONandUpdateUI(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseJSONandUpdateUI(error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("apikey", apikey);
                params.put("user_id", String.valueOf(userID));

                return params;
            }
        };
        queue.add(sr);

    }
    public void parseJSONandUpdateUI(String response){
       // Toast.makeText(this,"Here",Toast.LENGTH_SHORT).show();


        try{
            JSONObject rootObject = new JSONObject(response);


            if (rootObject.has("error")) {

                String err =rootObject.getString("error");
                Toast.makeText(this,err,Toast.LENGTH_SHORT).show();
            }
            else if(rootObject.has("xp")&& rootObject.has("lvl")&& rootObject.has("hiscore")){
                userLVL = rootObject.getInt("lvl");
                userHiscore = rootObject.getInt("hiscore");
                userXP = rootObject.getInt("xp");
                Toast.makeText(this,"Done",Toast.LENGTH_SHORT).show();

           TextView lvlTextView = findViewById(R.id.lvlTextView);
            TextView hiscoreTextView = findViewById(R.id.hiscoreTextView);
             lvlTextView.setText("LVL "+userLVL);
              hiscoreTextView.setText("Best score: "+userHiscore);
            }
            else{
                Toast.makeText(this,"No data to update",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(this,"User data update failed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public void getScores(View view){
        buttonHandler(0);
        String url= "https://augergames.com/xmasfall/api.php?scores=all&apikey="+apikey;
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {
                    String period = "All time best";
                    updateScoreboard(response,period);
                },   error -> {  Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show();
        });     queue.add(JSONRequest);
    }
    public void getYearScores(View view){

        buttonHandler(1);
        String url= "https://augergames.com/xmasfall/api.php?scores=year&apikey="+apikey;
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {
                    String period = "Year's best";
                    updateScoreboard(response,period);
                },   error -> {  Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show();
        });     queue.add(JSONRequest);
    }

    public void getMonthScores(View view){
        buttonHandler(2);
        String url= "https://augergames.com/xmasfall/api.php?scores=month&apikey="+apikey;
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {
                    String period = "Month's best";
                    updateScoreboard(response,period);
                },   error -> {  Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show();
        });     queue.add(JSONRequest);
    }
    public void getWeekScores(View view){
        buttonHandler(3);
        String url= "https://augergames.com/xmasfall/api.php?scores=week&apikey="+apikey;
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {

            String period = "Week's best";
                    updateScoreboard(response,period );
                },   error -> {  Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show();
        });     queue.add(JSONRequest);
    }
    public void getDayScores(View view){
        buttonHandler(4);
        String url= "https://augergames.com/xmasfall/api.php?scores=day&apikey="+apikey;
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {

                    String period = "Day's best";
                    updateScoreboard(response,period );
                },   error -> {  Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show();
        });     queue.add(JSONRequest);
    }
    public void updateScoreboard ( JSONObject response, String period){
       // Toast.makeText(this,"updateScoreboard",Toast.LENGTH_SHORT).show();
        TextView tableHeaderTV = findViewById(R.id.tableHeaderTV);
            TextView nthTabTV = findViewById(R.id.nthTabTV);
            TextView scoreTabTV = findViewById(R.id.scoreTabTV);
            TextView userTabTV = findViewById(R.id.userTabTV);
            TextView lvlTabTV = findViewById(R.id.lvlTabTV);
            TextView dateTabTV = findViewById(R.id.dateTabTV);

        String tableHeader = period;
        tableHeaderTV.setText(tableHeader);

        StringBuilder scoresItem = new StringBuilder(getString(R.string.score) + "\n");
        StringBuilder userItem = new StringBuilder(getString(R.string.username) + "\n");
        StringBuilder lvlItem = new StringBuilder(getString(R.string.lvl) + "\n");
        StringBuilder dateItem = new StringBuilder(getString(R.string.date) + "\n");
        StringBuilder nthItem = new StringBuilder("#" + "\n");

        try{
            JSONArray forecastList = response.getJSONArray("scores");

            for(int i = 0; i< ((JSONArray) forecastList).length(); i++){

                JSONObject scoreItem = forecastList.getJSONObject(i);
                int score = scoreItem.getInt("hiscore");
                String uname = scoreItem.getString("uname");
                int lvl = scoreItem.getInt("lvl");
                String date = scoreItem.getString("date");

                //
                DateFormat inputFormat = new SimpleDateFormat("yyyy-mm-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd.mm.yyyy");
                Date newDate = inputFormat.parse(date);
                assert newDate != null;
                String outputDateStr = outputFormat.format(newDate);
                //

                nthItem.append("\n").append(i);
                scoresItem.append("\n").append(score);
                userItem.append("\n").append(uname);
                lvlItem.append("\n").append(lvl);
                dateItem.append("\n").append(outputDateStr);

            }
            nthItem.append("\n");
            scoreTabTV.setText(scoresItem);
            userTabTV.setText(userItem.toString());
            lvlTabTV.setText(lvlItem.toString());
            dateTabTV.setText(dateItem.toString());
            nthTabTV.setText(nthItem.toString());

        }
        catch(JSONException | ParseException e){

            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
            // Toast.makeText(this,response.toString(),Toast.LENGTH_SHORT).show();

        }


    }
}