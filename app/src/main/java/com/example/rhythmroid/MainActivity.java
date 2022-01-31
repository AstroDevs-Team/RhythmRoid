package com.example.rhythmroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView music_cover, main_bg_blurred;

    private Palette.Swatch vibrantSwatch;
    private Palette.Swatch darkVibrantSwatch;
    private Palette.Swatch lightMutedSwatch;
    private Palette.Swatch darkMutedSwatch;

    TextView musicInfo_tv, main_length, current_length;
    SeekBar bar_music;
    RequestQueue queue;
    Runnable runnableCheckSong;
    Handler handlerCheckSong;
    String metaToCheck;
    static String sendReqIp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        music_cover.setClipToOutline(true);

        bar_music.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                set_position(seekBar.getProgress());
            }
        });


    }

    private void init() {

        queue = Volley.newRequestQueue(this);
        music_cover = findViewById(R.id.music_image_show);
        main_bg_blurred = findViewById(R.id.main_bg);
        musicInfo_tv = findViewById(R.id.musicInfo_tv);
        main_length = findViewById(R.id.main_length);
        bar_music = findViewById(R.id.bar_music);
        current_length = findViewById(R.id.current_length);

        get_pic("/static/default.jpg");

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        sendReqIp = sharedPreferences.getString("localhost", "127.0.0.1");

        interactive();
    }

    public void start_progress(int position) {

        int current_position = position;

        int current_sec = current_position % 60;
        int current_min = (current_position - current_sec) / 60;

        String temp = "";
        if (current_sec < 10) {
            if (current_min < 10) {
                temp = String.format(Locale.getDefault(),
                        "0%s:0%s",
                        current_min, current_sec);
            } else {
                temp = String.format(Locale.getDefault(),
                        "%s:0%s",
                        current_min, current_sec);
            }
        } else {
            if (current_min < 10) {
                temp = String.format(Locale.getDefault(),
                        "0%s:%s",
                        current_min, current_sec);
            } else {
                temp = String.format(Locale.getDefault(),
                        "%s:%s",
                        current_min, current_sec);
            }
        }
        current_length.setText(temp);
    }

    public void set_position(int position) {
        StringRequest requestPlay = new StringRequest(Request.Method.GET, "http://" + sendReqIp + ":8000/play_actions?action=seek&position=" + position,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        queue.add(requestPlay);
    }

    public void get_pic(String url) {

        Picasso.get().load("http://" + sendReqIp + ":8000" + url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Drawable d = new BitmapDrawable(getResources(), bitmap);
                music_cover.setImageDrawable(d);
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@Nullable Palette palette) {

                        vibrantSwatch = palette.getVibrantSwatch();
                        lightMutedSwatch = palette.getLightMutedSwatch();
                        darkMutedSwatch = palette.getDarkMutedSwatch();
                        darkVibrantSwatch = palette.getDarkVibrantSwatch();

                        ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());
                        try {
                            mDrawable.getPaint().setShader(new LinearGradient(600, 100, main_bg_blurred.getHeight(), main_bg_blurred.getWidth(),
                                    darkMutedSwatch != null ? darkMutedSwatch.getRgb() : darkVibrantSwatch.getRgb(),
                                    vibrantSwatch != null ? vibrantSwatch.getRgb() : lightMutedSwatch.getRgb(), Shader.TileMode.REPEAT));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        main_bg_blurred.setBackground(mDrawable);


                    }
                });
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                music_cover.setImageDrawable(placeHolderDrawable);
            }
        });

    }

    public void interactive() {
        handlerCheckSong = new Handler();
        runnableCheckSong = new Runnable() {
            @Override
            public void run() {
                    StringRequest requestPlay = new StringRequest(Request.Method.GET, "http://" + sendReqIp + ":8000/playing_info",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject main = new JSONObject(response.trim());
                                        JSONObject meta = main.getJSONObject("meta");
                                        int current_sec = main.getInt("position");
                                        bar_music.setProgress(current_sec);
                                        start_progress(current_sec);
                                        boolean isPlaying = main.getBoolean("playing");
                                        try {
                                            if (!metaToCheck.equals(meta.toString())) {
                                                StringRequest requestPlay = new StringRequest(Request.Method.GET, "http://" + sendReqIp + ":8000/play_actions?action=" + (isPlaying ? "play" : "pause"),
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject main = new JSONObject(response.trim());
                                                                    musicInfo(main);
                                                                    musicInfoCheck();
                                                                    ImageButton img_btn = findViewById(R.id.pl_ps_btn);
                                                                    if (isPlaying) {
                                                                        img_btn.setImageResource(R.drawable.ic_baseline_pause_24);
                                                                        img_btn.setTag("pause");
                                                                    } else {
                                                                        img_btn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                                                        img_btn.setTag("play");
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                if (error.toString().equals("com.android.volley.ServerError")) {
                                                                    startActivity(new Intent(MainActivity.this, GetIp.class));
                                                                    MainActivity.this.finish();
                                                                }
                                                            }
                                                        }
                                                );
                                                queue.add(requestPlay);
                                            } else if (metaToCheck.equals(meta.toString())) {
                                                StringRequest requestPlay = new StringRequest(Request.Method.GET, "http://" + sendReqIp + ":8000/play_actions?action=" + (isPlaying ? "play" : "pause"),
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject main = new JSONObject(response.trim());
                                                                    ImageButton img_btn = findViewById(R.id.pl_ps_btn);
                                                                    if (isPlaying) {
                                                                        img_btn.setImageResource(R.drawable.ic_baseline_pause_24);
                                                                        img_btn.setTag("pause");
                                                                    } else {
                                                                        img_btn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                                                        img_btn.setTag("play");
                                                                    }
                                                                    musicInfo(main);
                                                                    musicInfoCheck();
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                if (error.toString().equals("com.android.volley.ServerError")) {
                                                                    startActivity(new Intent(MainActivity.this, GetIp.class));
                                                                    MainActivity.this.finish();
                                                                }
                                                            }
                                                        }
                                                );
                                                queue.add(requestPlay);
                                            }
                                        } catch (NullPointerException e) {
                                            StringRequest requestPlay = new StringRequest(Request.Method.GET, "http://" + sendReqIp + ":8000/play_actions?action=" + (isPlaying ? "play" : "pause"),
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {
                                                                JSONObject main = new JSONObject(response.trim());
                                                                musicInfo(main);
                                                                musicInfoCheck();
                                                                ImageButton img_btn = findViewById(R.id.pl_ps_btn);
                                                                if (isPlaying) {
                                                                    img_btn.setImageResource(R.drawable.ic_baseline_pause_24);
                                                                    img_btn.setTag("pause");
                                                                } else {
                                                                    img_btn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                                                    img_btn.setTag("play");
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            if (error.toString().equals("com.android.volley.ServerError")) {
                                                                startActivity(new Intent(MainActivity.this, GetIp.class));
                                                                MainActivity.this.finish();
                                                            }
                                                        }
                                                    }
                                            );
                                            queue.add(requestPlay);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error.toString().equals("com.android.volley.ServerError")) {
                                        startActivity(new Intent(MainActivity.this, GetIp.class));
                                        MainActivity.this.finish();
                                    }
                                }
                            }
                    );
                    queue.add(requestPlay);

                    handlerCheckSong.postDelayed(this, 400);
                }


        };
        handlerCheckSong.postDelayed(runnableCheckSong, 400);
    }

    public void pl_ps_toggle(View view) {

        switch (view.getTag().toString()) {
            case "play":
                StringRequest requestPlay = new StringRequest(Request.Method.GET, "http://" + sendReqIp + ":8000/play_actions?action=play",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject main = new JSONObject(response.trim());
                                    musicInfo(main);
                                    ImageView imgV = (ImageView) view;
                                    imgV.setImageResource(R.drawable.ic_baseline_pause_24);
                                    imgV.setTag("pause");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }
                );
                queue.add(requestPlay);
                break;

            case "pause":
                StringRequest requestPause = new StringRequest(Request.Method.GET, "http://" + sendReqIp + ":8000/play_actions?action=pause",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject main = new JSONObject(response.trim());
                                    musicInfo(main);
                                    ImageView imgV = (ImageView) view;
                                    imgV.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                    imgV.setTag("play");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }
                );
                queue.add(requestPause);
                break;
        }

    }

    public void previous_song(View view) {
        StringRequest requestPause = new StringRequest(Request.Method.GET, "http://" + sendReqIp + ":8000/play_actions?action=previous",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject main = new JSONObject(response.trim());
                            musicInfo(main);
                            ImageButton img_btn = findViewById(R.id.pl_ps_btn);
                            img_btn.setImageResource(R.drawable.ic_baseline_pause_24);
                            img_btn.setTag("pause");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        queue.add(requestPause);
    }

    public void next_song(View view) {

        StringRequest requestPause = new StringRequest(Request.Method.GET, "http://" + sendReqIp + ":8000/play_actions?action=next",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject main = new JSONObject(response.trim());
                            musicInfo(main);
                            ImageView imgV = (ImageView) view;
                            ImageButton img_btn = findViewById(R.id.pl_ps_btn);
                            img_btn.setImageResource(R.drawable.ic_baseline_pause_24);
                            img_btn.setTag("pause");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        queue.add(requestPause);


    }

    public void musicInfoCheck() {

        StringRequest requestPause = new StringRequest(Request.Method.GET, "http://" + sendReqIp + ":8000/playing_info",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject main = new JSONObject(response.trim());
                            JSONObject meta = main.getJSONObject("meta");
                            metaToCheck = meta.toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        queue.add(requestPause);
    }

    public void musicInfo(JSONObject data) {

        try {
            JSONObject meta = data.getJSONObject("status").getJSONObject("meta");
            String music_name = meta.getString("title");
            String singer = meta.getString("singer");
            String singer_music = music_name + " - " + singer;
            String image_url = meta.getString("image");
            String main_length_str = meta.getString("time");
            int length_sec = meta.getInt("timebyseconds");

            bar_music.setMax(length_sec);
            main_length.setText(main_length_str);
            get_pic(image_url);
            musicInfo_tv.setText(singer_music);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onPause() {
        super.onPause();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}