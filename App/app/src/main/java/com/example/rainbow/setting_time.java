package com.example.rainbow;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class setting_time  extends AppCompatActivity {
    TextView goal_text;
    TimePicker timepicker;
    int hour, minutes;
    Button GetTime;
    String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_time_page);

        Intent intent = getIntent();
        today = intent.getExtras().getString("today");


        //데이터베이스 설정
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/last/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RainbowAPI mRetrofitAPI = mRetrofit.create(RainbowAPI.class);

        Call<PostItem> mCallMoviewList = mRetrofitAPI.getGoalTime(today);
        mCallMoviewList.enqueue(new Callback<PostItem>() {
            @Override
            public void onResponse(Call<PostItem> call, Response<PostItem> response) {
                PostItem result = response.body();
                int settingtime = result.getTarget();

                settingtime /= 60;
                int settinghours, settingminutes;
                settinghours = settingtime /60;
                settingminutes = settingtime %60;

                //목표시간 텍스트 설정
                final TextView goal_time = (TextView)findViewById(R.id.goal_time);
                String goaltxt = settinghours+"시간 "+settingminutes+"분";
                goal_time.setText(goaltxt);

                //텍스트 꾸미기
                TextPaint paint2 = goal_time.getPaint();
                float width2 = paint2.measureText(goaltxt);
                Shader textShader2 = new LinearGradient(0, 0, width2, goal_time.getTextSize(),
                        new int[]{
                                Color.parseColor("#fa5050"),
                                Color.parseColor("#faa850"),
                                Color.parseColor("#66ed5f"),
                                Color.parseColor("#60b4f0"),
                                Color.parseColor("#c15df0"),
                        }, null, Shader.TileMode.MIRROR);
                goal_time.getPaint().setShader(textShader2);

            }
            @Override
            public void onFailure(Call<PostItem> call, Throwable t) {
                t.printStackTrace();
            }
        });


        timepicker = (TimePicker)findViewById(R.id.timePicker1);
        goal_text = (TextView)findViewById(R.id.goal_time);
        GetTime = (Button)findViewById(R.id.button1);

        timepicker.setIs24HourView(true);


        GetTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                hour = timepicker.getCurrentHour();
                minutes = timepicker.getCurrentMinute();

                Retrofit mRetrofit = new Retrofit.Builder()
                        .baseUrl("https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/last/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RainbowAPI mRetrofitAPI = mRetrofit.create(RainbowAPI.class);

                PostGoal newGoal= new PostGoal();
                newGoal.setTime((hour*60+minutes)*60);
                newGoal.setDate(today);

                Call<PostItemGoal> mCallMoviewList2 = mRetrofitAPI.postGoal(newGoal);
                mCallMoviewList2.enqueue(new Callback<PostItemGoal>() {
                    @Override
                    public void onResponse(Call<PostItemGoal> call, Response<PostItemGoal> response) {
                        PostItemGoal result = response.body();
                        // 위에 표시될 텍스트 설정한 시간으로 재설정
                        TextView goal_time  = (TextView) findViewById(R.id.goal_time);
                        String goaltxt2= hour+"시간 "+minutes+"분";
                        goal_time.setText(goaltxt2);

                        //재설정한 텍스트 꾸미기
                        TextPaint paint = goal_text .getPaint();
                        float width = paint.measureText(goaltxt2);
                        Shader textShader = new LinearGradient(0, 0, width, goal_text .getTextSize(),
                                new int[]{
                                        Color.parseColor("#fa5050"),
                                        Color.parseColor("#faa850"),
                                        Color.parseColor("#66ed5f"),
                                        Color.parseColor("#60b4f0"),
                                        Color.parseColor("#c15df0"),
                                }, null, Shader.TileMode.CLAMP);
                        goal_text .getPaint().setShader(textShader);
                    }
                    @Override
                    public void onFailure(Call<PostItemGoal> call, Throwable t) {
                        t.printStackTrace();
                    }
                });

            }
        });


    }
}