package com.example.rainbow;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class date_study extends AppCompatActivity {
    TextView tx1;
    private Retrofit mRetrofit;
    private RainbowAPI mRetrofitAPI;

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_study_page);

        //툴바 세팅
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // 날짜 값 전 페이지로부터 넘겨 받기
        Intent intent = getIntent();
        tx1 = (TextView)findViewById(R.id.today_date);
        int year = intent.getExtras().getInt("Year");
        int month = intent.getExtras().getInt("Month")+1;
        int day = intent.getExtras().getInt("Day");
        String date = Integer.toString(year)+"년 " + Integer.toString(month)+"월 " + Integer.toString(day) + "일";
        tx1.setText(date);

        //날짜 색바꾸기
        TextPaint paint1 = tx1.getPaint();
        float width1 = paint1.measureText(date);
        Shader textShader1 = new LinearGradient(0, 0, width1, tx1.getTextSize(),
                new int[]{
                        Color.parseColor("#fa5050"),
                        Color.parseColor("#faa850"),
                        Color.parseColor("#66ed5f"),
                       Color.parseColor("#60b4f0"),
                        Color.parseColor("#c15df0"),
                }, null, Shader.TileMode.MIRROR);
        tx1.getPaint().setShader(textShader1);

        // 내장 데이터베이스 연결
        final DataBaseHelper DBHelper = new DataBaseHelper(this);
        // 1-9일은 Int로 표현했을 때, 01, 09이렇게 표현안되므로 string으로 변경
        String month_s = Integer.toString(month);
        if(month_s.length() == 1)
            month_s = "0" + month;
        String day_s = Integer.toString(day);
        if(day_s.length() == 1)
            day_s = "0" + day;

        //string으로 변경한 날짜로 공부시간, 목표시간 구해서 분단위 int로 넣기(내장디비)
        //int studytime =  mMyAPI.get_study_time(year+"-"+month_s+"-"+day_s);
        //int goaltime = mMyAPI.get_study_goal(year+"-"+month_s+"-"+day_s);
        //int studytime = DBHelper.getStudyTime(year+"-"+month_s+"-"+day_s);
        //int goaltime = DBHelper.getStudyGoal(year+"-"+month_s+"-"+day_s);

        //데이터베이스 설정
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/last/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RainbowAPI mRetrofitAPI = mRetrofit.create(RainbowAPI.class);

        //목표시간 불러오기
        Call<PostItem> mCallMoviewList = mRetrofitAPI.getGoalTime(year + "-" + month_s + "-" + day_s);
        mCallMoviewList.enqueue(new Callback<PostItem>() {
            @Override
            public void onResponse(Call<PostItem> call, Response<PostItem> response) {
                PostItem result = response.body();
                int goaltime = result.getTarget();
                goaltime /= 60;
                TextView tx = (TextView) findViewById(R.id.study_time);
                tx.setText(Integer.toString(goaltime));
            }
            @Override
            public void onFailure(Call<PostItem> call, Throwable t) {
                t.printStackTrace();
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Call<PostItem> mCallMoviewList2 = mRetrofitAPI.getStudyTime(year + "-" + month_s + "-" + day_s);
        mCallMoviewList2.enqueue(new Callback<PostItem>() {
            @Override
            public void onResponse(Call<PostItem> call, Response<PostItem> response) {
                PostItem result = response.body();
                int studytime = result.getTarget();
                studytime /= 60;

                int studyhours, studyminutes;
                studyhours = studytime / 60;
                studyminutes = studytime % 60;

                TextView tx = (TextView) findViewById(R.id.study_time);
                int goaltime = Integer.parseInt((String) tx.getText());
                int goalhours, goalminutes;
                goalhours = goaltime / 60;
                goalminutes = goaltime % 60;

                // 공부시간/목표시간
                String studytxt = studyhours + "시간 " + studyminutes + "분 / " + goalhours + "시간 " + goalminutes + "분";
                tx.setText(studytxt);

                // 프로그래스바 세팅
                ProgressBar pb = (ProgressBar) findViewById(R.id.study_progressbar);
                pb.setProgress(studytime * 100 / goaltime);

                TextPaint paint2 = tx.getPaint();
                float width2 = paint2.measureText(studytxt);
                Shader textShader2 = new LinearGradient(0, 0, width2, tx.getTextSize(),
                        new int[]{
                                Color.parseColor("#fa5050"),
                                Color.parseColor("#faa850"),
                                Color.parseColor("#66ed5f"),
                                Color.parseColor("#60b4f0"),
                                Color.parseColor("#c15df0"),
                        }, null, Shader.TileMode.MIRROR);
                tx.getPaint().setShader(textShader2);
            }

            @Override
            public void onFailure(Call<PostItem> call, Throwable t) {
                t.printStackTrace();
            }
        });

        // 설정한 날짜로 detect 횟수 받아오기(내장디비)
        //int numofdetect = DBHelper.getDetectNum(year+"-"+month_s+"-"+day_s);
        //int numofdetect = mMyAPI.get_detected_time(year+"-"+month_s+"-"+day_s);
        // 00회
        // TextView tx3 = (TextView)findViewById(R.id.phone_time);
        //String detecttxt = numofdetect+"회";
        //tx3.setText(detecttxt);

        Call<PostItem> mCallMoviewList3 = mRetrofitAPI.getInterruptTime(year+"-"+month_s+"-"+day_s);
        mCallMoviewList3.enqueue(new Callback<PostItem>() {
            @Override
            public void onResponse(Call<PostItem> call, Response<PostItem> response) {
                PostItem result = response.body();
                int interrupttime = result.getTarget();
                TextView tx = (TextView)findViewById(R.id.phone_time);
                String interrupttxt = Integer.toString(interrupttime)+"회";
                tx.setText(interrupttxt);

                TextPaint paint3 = tx.getPaint();
                float width3 = paint3.measureText(interrupttxt);

                Shader textShader3 = new LinearGradient(0, 0, width3, tx.getTextSize(),
                        new int[]{
                                Color.parseColor("#fa5050"),
                                Color.parseColor("#faa850"),
                                Color.parseColor("#66ed5f"),
                                Color.parseColor("#60b4f0"),
                                Color.parseColor("#c15df0"),
                        }, null, Shader.TileMode.MIRROR);
                tx.getPaint().setShader(textShader3);
            }
            @Override
            public void onFailure(Call<PostItem> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
