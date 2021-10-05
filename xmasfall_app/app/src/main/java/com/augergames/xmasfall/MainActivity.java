package com.augergames.xmasfall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(savedInstanceState == null){
            //it is the first time the fragment is being called

        }else{
            //not the first time so we will check SavedInstanceState bundle

            TextView signupUsernameEditText = findViewById(R.id.signupUsernameEditText);
            signupUsernameEditText.setText(savedInstanceState.getString("signupUsernameEditText"));

            TextView temperaturetxt = findViewById(R.id.signupPasswordEditText);
            temperaturetxt.setText(savedInstanceState.getString("signupPasswordEditText"));

            TextView signupRePasswordEditText = findViewById(R.id.signupRePasswordEditText);
            signupRePasswordEditText.setText(savedInstanceState.getString("signupRePasswordEditText"));

        }

        queue = Volley.newRequestQueue(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        TextView signupUsernameEditText = findViewById(R.id.signupUsernameEditText);
        savedInstanceState.putString("signupUsernameEditText", signupUsernameEditText.getText().toString());

        TextView signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        savedInstanceState.putString("signupPasswordEditText", signupPasswordEditText.getText().toString());

        TextView signupRePasswordEditText = findViewById(R.id.signupRePasswordEditText);
        savedInstanceState.putString("signupRePasswordEditText", signupRePasswordEditText.getText().toString());

    }
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        TextView signupUsernameEditText = findViewById(R.id.signupUsernameEditText);
        signupUsernameEditText.setText(savedInstanceState.getString("signupUsernameEditText"));

        TextView signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        signupPasswordEditText.setText(savedInstanceState.getString("signupPasswordEditText"));

        TextView signupRePasswordEditText = findViewById(R.id.signupRePasswordEditText);
        signupRePasswordEditText.setText(savedInstanceState.getString("signupRePasswordEditText"));

    }
    public void postSignup(View view){
        Toast.makeText(this,"postSignup",Toast.LENGTH_SHORT).show();

        String postUrl ="https://augergames.com/xmasfall/api.php";
        String apikey = "a7e15036691751d80a4ac8d4f005b4b7";

        //
        TextView signupUsernameEditText = findViewById(R.id.signupUsernameEditText);
        TextView signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        TextView signupRePasswordEditText = findViewById(R.id.signupRePasswordEditText);


        /////

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
                    params.put("username", signupUsernameEditText.getText().toString());
                    params.put("password", signupPasswordEditText.getText().toString());
                    params.put("rpassword", signupRePasswordEditText.getText().toString());

                    return params;
                }


            };
            queue.add(sr);
        }
        ////




    public void parseJSONandUpdateUI(String response){
        TextView debug = findViewById(R.id.debug);
        debug.setText(response);

    }
}