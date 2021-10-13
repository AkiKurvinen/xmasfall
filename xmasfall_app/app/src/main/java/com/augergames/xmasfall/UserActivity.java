package com.augergames.xmasfall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class UserActivity extends AppCompatActivity {
    private int userID = 0;
    private String userName = "Guest";
    private int userLVL = 0;
    private int userXP = 0;
    private String key = "";
    private String userURL = "https://augergames.com/xmasfall/autologin.php";
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();



        if (extras != null) {
            userID = intent.getIntExtra("userID", 0);
            userName = intent.getStringExtra("userName");
            userLVL =  intent.getIntExtra("userLVL", 0);
            userXP = intent.getIntExtra("userXP", 0);
            key = intent.getStringExtra("key");
            userURL= userURL+"?key="+key;
        }
        queue = Volley.newRequestQueue(this);
        getScores();
      //  TextView debug = findViewById(R.id.textView);
     //   debug.setText(userURL);

    }
    public void gotoGame(View view){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userURL)));

    }

    public void getScores(){
        String url= "https://augergames.com/xmasfall/api.php?scores&apikey=a7e15036691751d80a4ac8d4f005b4b7";
      //  String url= "https://api.openweathermap.org/data/2.5/forecast?q=tampere&units=metric&appid=22ba8ea1a4113ec446bbb83bccc40c5c";
        JsonObjectRequest JSONRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {

                    updateScoreboard(response);
                },   error -> {  Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show();
        });     queue.add(JSONRequest);
    }
    public void updateScoreboard ( JSONObject response){


        String scoreItem ="All time best\n";
        try{
            JSONArray forecastList = response.getJSONArray("scores");
            for(int i = 0; i< ((JSONArray) forecastList).length(); i++){
                JSONObject weatherItem = forecastList.getJSONObject(i);


                String date = weatherItem.getString("date");

                scoreItem  += date+"\n";
            }
            TextView weatherForecTextView = findViewById(R.id.textView2);

            weatherForecTextView.setText("");
            weatherForecTextView.append(scoreItem+"\n");
        }
        catch(JSONException e){

            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
            // Toast.makeText(this,response.toString(),Toast.LENGTH_SHORT).show();

        }


    }
}