package com.example.rhythmroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.royrodriguez.transitionbutton.TransitionButton;

public class GetIp extends AppCompatActivity {

    private TransitionButton transitionButton;
    EditText ip_get_ed;
    String ip_to_check = "";

    RequestQueue queue;
    boolean isSuccessful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_ip);

        transitionButton = findViewById(R.id.transition_button);
        ip_get_ed = findViewById(R.id.ip_ed);
        queue = Volley.newRequestQueue(this);
        transitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the loading animation when the user tap the button
                transitionButton.startAnimation();
                View view = GetIp.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                // Do your networking task or background work here.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ip_to_check = ip_get_ed.getText().toString();
                        if (ip_to_check.isEmpty()) {
                            Toast.makeText(GetIp.this, "Please enter an ip to continue", Toast.LENGTH_SHORT).show();
                            transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        } else {
                            checkIp(ip_to_check);
                        }
                    }
                }, 2000);
            }
        });
    }

    public void checkIp(String ip) {
        StringRequest requestCheck = new StringRequest(Request.Method.GET, "http://" + ip + ":8000/playing_info",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("localhost" , ip);
                        myEdit.apply();
                        transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                            @Override
                            public void onAnimationStopEnd() {
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String error_context = error.toString();
                        if (error_context.equals("com.android.volley.ServerError")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(GetIp.this);
                            builder.setTitle("No song is playing!");
                            builder.setMessage("Your Ip address looks correct, but there is no song playing in RhythmBox");
                            builder.setPositiveButton("Ok", null);
                            builder.show();

                            transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(GetIp.this);
                            builder.setTitle("incorrect Ip address!");
                            builder.setMessage("No data received from entered ip, please check your ip address then try again!");
                            builder.setPositiveButton("Ok", null);
                            builder.show();

                            transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    }
                }

        );
        queue.add(requestCheck);

    }


}
