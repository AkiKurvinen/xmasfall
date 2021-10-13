package com.augergames.xmasfall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity {
    private int userID = 0;
    private String userName = "Guest";
    private int userLVL = 0;
    private int userXP = 0;
    private String key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        String userURL = "https://augergames.com/xmasfall/autologin.php";

        //
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            userID = intent.getIntExtra("userID", 0);
            userName = intent.getStringExtra("userName");
            userLVL =  intent.getIntExtra("userLVL", 0);
            userXP = intent.getIntExtra("userXP", 0);
            key = intent.getStringExtra("key");

            userURL= userURL+"?key="+key;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userURL)));

        }
        else{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userURL)));
        }
        TextView debug = findViewById(R.id.textView);
        debug.setText(userURL);

        //



      //  startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://augergames.com/xmasfall/redirect.php")));


    }
}