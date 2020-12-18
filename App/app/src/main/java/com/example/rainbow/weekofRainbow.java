package com.example.rainbow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* textView id : week_1, week_2...  ImageView id : week_pic_1, week_pic_2....
무지개 : rb_pic7
반무지개 : half_rb_pic4
비 : rain_pic5
기본값 : rb_pic8
변수명 week_rbpic1,...
*/
public class weekofRainbow extends AppCompatActivity {
    private ImageView week_rbpic1, week_rbpic2, week_rbpic3, week_rbpic4;
    private TextView week1, week2, week3, week4;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekofrainbow_page);

        week_rbpic1 = (ImageView)findViewById(R.id.week_pic_1);
        week_rbpic2 = (ImageView)findViewById(R.id.week_pic_2);
        week_rbpic3 = (ImageView)findViewById(R.id.week_pic_3);
        week_rbpic4 = (ImageView)findViewById(R.id.week_pic_4);

        //pic 숨겨놓기
        week_rbpic1.setImageResource(0);
        week_rbpic2.setImageResource(0);
        week_rbpic3.setImageResource(0);
        week_rbpic4.setImageResource(0);

        week1 = (TextView) findViewById(R.id.week_1);
        week2 = (TextView) findViewById(R.id.week_2);
        week3 = (TextView) findViewById(R.id.week_3);
        week4 = (TextView) findViewById(R.id.week_4);


        // 내장데이터베이스 연결 및 일주일간 성취도 불러오기
        //final DataBaseHelper DBHelper = new DataBaseHelper(this);
        // 일주일마다의 성취도를 int배열 형태로 불러옴
        // 3: 7일 모두 달성, 2: 4-6일 달성, 1:3일이하 달성
        // 예시) [3,2,1]
        //int result[] = DBHelper.getWeekAchieve();

        Intent intent = getIntent();
        String previous = intent.getExtras().getString("previous");
        String today = intent.getExtras().getString("today");

        //데이터베이스 설정
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/last/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RainbowAPI mRetrofitAPI = mRetrofit.create(RainbowAPI.class);

        Call<PostItemEstimation> mCallMoviewList = mRetrofitAPI.getInterruptTime(previous, today);
        mCallMoviewList.enqueue(new Callback<PostItemEstimation>() {
            @Override
            public void onResponse(Call<PostItemEstimation> call, Response<PostItemEstimation> response) {
                PostItemEstimation resultEstimation = response.body();
                int[] result = resultEstimation.getTarget();

                week1.setText("3주 전");
                if (result[0] == 3) // 7일 모두 달성한 경우
                    week_rbpic1.setImageResource(R.drawable.rb_pic7);
                else if (result[0] == 2) // 4-6일 달성한 경우
                    week_rbpic1.setImageResource(R.drawable.half_rb_pic4);
                else    // 3일 이하 달성한 경우
                    week_rbpic1.setImageResource(R.drawable.rain_pic5);


                week2.setText("2주 전");
                if (result[1] == 3)
                    week_rbpic2.setImageResource(R.drawable.rb_pic7);
                else if (result[1] == 2)
                    week_rbpic2.setImageResource(R.drawable.half_rb_pic4);
                else
                    week_rbpic2.setImageResource(R.drawable.rain_pic5);

                week3.setText("1주 전");
                if (result[2] == 3)
                    week_rbpic3.setImageResource(R.drawable.rb_pic7);
                else if (result[2] == 2)
                    week_rbpic3.setImageResource(R.drawable.half_rb_pic4);
                else
                    week_rbpic3.setImageResource(R.drawable.rain_pic5);

            // 빨 - 주 - 초 - 파 - 보
                week1.setTextColor(Color.parseColor("#eb4934"));
                week2.setTextColor(Color.parseColor("#eba834"));
                week3.setTextColor(Color.parseColor("#3bba14"));
                week4.setTextColor(Color.parseColor("#1288cc"));
            //week5까지 안나와서 색 수정 필요
            //week5.setTextColor(Color.parseColor("#a753f5"));

            }
            @Override
            public void onFailure(Call<PostItemEstimation> call, Throwable t) {
                t.printStackTrace();
            }
        });


        // 툴바 지정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  Rainbow");
    }

}
