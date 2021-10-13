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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


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
    public void buttonHandler(int btn){
        Button buttonAll = findViewById(R.id.buttonAll);
        Button buttonYear = findViewById(R.id.buttonYear);
        Button buttonMonth = findViewById(R.id.buttonMonth);
        Button buttonWeek = findViewById(R.id.buttonWeek);

        if(btn==0){
            buttonAll.setEnabled(false);
            buttonYear.setEnabled(true);
            buttonMonth.setEnabled(true);
            buttonWeek.setEnabled(true);
        }
        else if(btn==1){
            buttonAll.setEnabled(true);
            buttonYear.setEnabled(false);
            buttonMonth.setEnabled(true);
            buttonWeek.setEnabled(true);
        }
        else if(btn==2){
            buttonAll.setEnabled(true);
            buttonYear.setEnabled(true);
            buttonMonth.setEnabled(false);
            buttonWeek.setEnabled(true);
        }
        else if(btn==3){
            buttonAll.setEnabled(true);
            buttonYear.setEnabled(true);
            buttonMonth.setEnabled(true);
            buttonWeek.setEnabled(false);
        }



    }
    public void gotoGame(View view){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userURL)));

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
    public void updateScoreboard ( JSONObject response, String period){
        Toast.makeText(this,"updateScoreboard",Toast.LENGTH_SHORT).show();
        TextView tableHeaderTV = findViewById(R.id.tableHeaderTV);
            TextView nthTabTV = findViewById(R.id.nthTabTV);
            TextView scoreTabTV = findViewById(R.id.scoreTabTV);
            TextView userTabTV = findViewById(R.id.userTabTV);
            TextView lvlTabTV = findViewById(R.id.lvlTabTV);
            TextView dateTabTV = findViewById(R.id.dateTabTV);

        String tableHeader = period;
        tableHeaderTV.setText(tableHeader);

        String scoresItem = getString(R.string.score)+"\n";
        String userItem = getString(R.string.username)+"\n";
        String lvlItem = getString(R.string.lvl)+"\n";
        String dateItem = getString(R.string.date)+"\n";
        String nthItem = "#"+"\n";

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
                String inputDateStr=date;
                Date newDate = inputFormat.parse(inputDateStr);
                String outputDateStr = outputFormat.format(newDate);
                //
                nthItem += "\n"+i;
                scoresItem  +="\n"+ score;
                userItem +="\n"+  uname;
                lvlItem +="\n"+  lvl;
                dateItem +="\n"+ outputDateStr;

            }

            scoreTabTV.setText(scoresItem);
            userTabTV.setText(userItem);
            lvlTabTV.setText(lvlItem);
            dateTabTV.setText(dateItem);
            nthTabTV.setText(nthItem);

        }
        catch(JSONException | ParseException e){

            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
            // Toast.makeText(this,response.toString(),Toast.LENGTH_SHORT).show();

        }


    }
}