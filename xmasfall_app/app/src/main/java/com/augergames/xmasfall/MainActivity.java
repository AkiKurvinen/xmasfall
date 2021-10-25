package com.augergames.xmasfall;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private int userID = 0;
    private String userName = "";
    private int userHiscore = 0;
    private int userLVL = 0;
    private int userXP = 0;
    private String key = "";
    private RequestQueue queue;
    private boolean isLogin = false;
    private String apikey = "key_goes_here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView infoMessageTextView = findViewById(R.id.infoMessageTextView);
        infoMessageTextView.setText("");

        TextView signupTextView = findViewById(R.id.signupHeaderTextView);
        EditText signupUsernameEditText = findViewById(R.id.signupUsernameEditText);
        EditText signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        EditText signupRePasswordEditText = findViewById(R.id.signupRePasswordEditText);
        Button signupButton = findViewById(R.id.signupButton);

        if(savedInstanceState != null){
            signupUsernameEditText.setText(savedInstanceState.getString("signupUsernameEditText"));
            signupPasswordEditText.setText(savedInstanceState.getString("signupPasswordEditText"));
            signupRePasswordEditText.setText(savedInstanceState.getString("signupRePasswordEditText"));

        }
        signupPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length() >0) {
                    signupButton.setEnabled(true);
                }
            }
        });
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

    public void postSignLogin(View view){
        if (!isLogin) {
            postSignup(view, isLogin);
        }
        else{
            postSignup(view, isLogin);
        }
    }
    public void postSignup(View view, boolean isLogin){

        String postUrl ="https://augergames.com/xmasfall/api.php";

        TextView signupUsernameEditText = findViewById(R.id.signupUsernameEditText);
        TextView signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        TextView signupRePasswordEditText = findViewById(R.id.signupRePasswordEditText);

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
                if(!isLogin){
                    params.put("rpassword", signupRePasswordEditText.getText().toString());
                }
                    return params;
                }
            };
            queue.add(sr);
        }

    public void toggleLoginSignup(View view){

        ImageView bgimageView = findViewById(R.id.bgimageView);

        if (!isLogin) {
            bgimageView.setImageResource(R.drawable.xmasbgright);
            gotoLogin(view);
        }
        else{
            bgimageView.setImageResource(R.drawable.xmasbgleft);
            gotoSignup(view);
        }
    }
    public void gotoLogin(View view){
        isLogin = true;
        TextView signupTextView = findViewById(R.id.signupHeaderTextView);
        signupTextView.setText(R.string.login);


        TextView signupRePasswordEditText = findViewById(R.id.signupRePasswordEditText);
        signupRePasswordEditText.setVisibility(View.INVISIBLE);

        TextView accountInfoTxt = findViewById(R.id.accountInfoTxt);
        accountInfoTxt.setText(R.string.dont_have_account);

        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setText(R.string.login);

        Button gotoLoginButton = findViewById(R.id.gotoLoginButton);
        gotoLoginButton.setText(R.string.sign_up_now);
    }
    public void gotoSignup(View view){
        isLogin = false;
        TextView signupTextView = findViewById(R.id.signupHeaderTextView);
        signupTextView.setText(R.string.sign_up_header);


        TextView signupRePasswordEditText = findViewById(R.id.signupRePasswordEditText);
        signupRePasswordEditText.setVisibility(View.VISIBLE);

        TextView accountInfoTxt = findViewById(R.id.accountInfoTxt);
        accountInfoTxt.setText(R.string.already_have_account);

        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setText(R.string.sign_up);

        Button gotoLoginButton = findViewById(R.id.gotoLoginButton);
        gotoLoginButton.setText(R.string.login_here);
    }
    public void parseJSONandUpdateUI(String response){
        TextView infoMessageTextView = findViewById(R.id.infoMessageTextView);
        infoMessageTextView.setText(response);

        try{
            JSONObject rootObject = new JSONObject(response);


            if (rootObject.has("error")) {
                String err =rootObject.getString("error");
                infoMessageTextView.setText(err);
            }
            else if(rootObject.has("id") && rootObject.has("uname")&& rootObject.has("xp")&& rootObject.has("lvl")){
                userID = rootObject.getInt("id");
                userName = rootObject.getString("uname");
                userLVL = rootObject.getInt("lvl");
                userHiscore = rootObject.getInt("hiscore");
                userXP = rootObject.getInt("xp");
                key = rootObject.getString("keyhash");

                if (userID != 0 && userName != ""){
                    Toast.makeText(this,getString(R.string.logged_in_as_toast)+ userName,Toast.LENGTH_SHORT).show();
                    infoMessageTextView.setText(userName+"");
                    gotoUserActivity();
                }
                else{
                    infoMessageTextView.setText(R.string.login_failed);
                }
            }

        } catch (JSONException e) {
            infoMessageTextView.append("Login failed");
            e.printStackTrace();
        }

    }
    public void gotoUserActivity() {
        Intent intent = new Intent(this, UserActivity.class);

        intent.putExtra("userID", userID);
        intent.putExtra("userName", userName);
        intent.putExtra("userLVL", userLVL);
        intent.putExtra("userXP", userXP);
        intent.putExtra("key", key);
        intent.putExtra("hiscore", userHiscore);

        startActivity(intent);
    }

    public void openDialog(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.dialog_title)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    intent.putExtra("userID", 0);
                    intent.putExtra("userName", "Guest");
                    intent.putExtra("userLVL", 0);
                    intent.putExtra("userXP", 0);
                    intent.putExtra("key", "");
                    intent.putExtra("hiscore", 0);
                    startActivity(intent);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        builder.show();

    }


}
