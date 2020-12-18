package com.example.rainbow;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.rainbow.decorator.FridayDecorator;
import com.example.rainbow.decorator.MondayDecorator;
import com.example.rainbow.decorator.SaturdayDecorator;
import com.example.rainbow.decorator.SundayDecorator;
import com.example.rainbow.decorator.ThursdayDecorator;
import com.example.rainbow.decorator.TuesdayDecorator;
import com.example.rainbow.decorator.WednesdayDecorator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.util.Calendar.*;

public class MainActivity extends AppCompatActivity implements OnDateSelectedListener {

    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private final Calendar calendar = Calendar.getInstance();
    MaterialCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        /*FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                //Log.e("HERE", "log success");
                if (!task.isSuccessful()) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                    return;
                }

                // Get new FCM registration token
                String token = task.getResult();

                // Log and toast
                //String msg = task.getResult().getToken();
                Log.d("FCM", token);

                Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
            }
        });*/

        // 달력 함수
        calendarView = (MaterialCalendarView) findViewById((R.id.calendarView));
        // 날짜 한 개만 클릭 가능
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        // 날짜 클릭가능하게 세팅
        calendarView.setClickable(true);
        // 오늘 날짜 하이라이트표시
        calendarView.setSelectedDate(CalendarDay.today());
        // 날짜 클릭 시, 함수 실행
        calendarView.setOnDateChangedListener(this);

        // 한달 전 날짜 구하기
        Calendar mon = getInstance();
        mon.add(DATE, -28);
        String beforeMonth = new SimpleDateFormat("yyyy-MM-dd").format(mon.getTime());
        // 달력 기본 설정
        calendarView.state().edit()
                // 주의 시작을 일요일
                .setFirstDayOfWeek(SUNDAY)
                // 캘린더의 범위 설정
                .setMinimumDate(mon)
                .setMaximumDate(CalendarDay.today())
                //Months로 볼 건지, Weeks로 볼 건지 결정
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        String[] param = makeDayForm();

        //데이터베이스 설정
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/last/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RainbowAPI mRetrofitAPI = mRetrofit.create(RainbowAPI.class);

        Call<PostItemStringList> mCallMoviewList = mRetrofitAPI.getAchievementList(0, param[0], param[1]);
        mCallMoviewList.enqueue(new Callback<PostItemStringList>() {
            @Override
            public void onResponse(Call<PostItemStringList> call, Response<PostItemStringList> response) {
                PostItemStringList result = response.body();
                new ApiSimulator(result.getTarget()).executeOnExecutor(Executors.newSingleThreadExecutor());
            }

            @Override
            public void onFailure(Call<PostItemStringList> call, Throwable t) {
                t.printStackTrace();
            }
        });


        // 디비: dot 표시하지 않을 날짜 지정(목표를 달성하지 못한 날)
        //final DataBaseHelper DBHelper = new DataBaseHelper(this);
        // 달성하지 못한날을 string배열 형태로 불러옴
        //String[] result = DBHelper.getNotAchievedDays();
        //result에 있는 날들에 점 찍기
        //new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());


        // 툴바 지정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  Rainbow");


        // SundayDecorator는 일요일에 지정색
        // oneDayDecorator는 오늘 날짜에 지정색
        // 닷은 모든 날짜에 빨주노초파남보로 있고, 목표를 달성하면 하나씩 없어지는 것으로 하였음!
        calendarView.addDecorators(
                new SundayDecorator(),
                new MondayDecorator(),
                new TuesdayDecorator(),
                new WednesdayDecorator(),
                new ThursdayDecorator(),
                new FridayDecorator(),
                new SaturdayDecorator(),
                new SundayDecorator(),
                oneDayDecorator);
    }

    // 닷 표시하는 함수
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result) {
            this.Time_Result = Time_Result;
        }

        // 점을 어떤 규칙으로 찍을
        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //현재 캘린더 불러오기
            Calendar calendar = Calendar.getInstance();
            //점을 찍을 day
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 -를 기준으로짜르고 string을 int 로 변환
            for (int i = 0; i < Time_Result.length; i++) {
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year, month - 1, dayy);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            if (isFinishing()) {
                return;
            }
            calendarView.addDecorator(new EventDecorator(calendarDays));
        }
    }


    // 날짜 클릭 시 일자 화면으로 이동
    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        // 일자 페이지로 이동
        Intent intent = new Intent(this, date_study.class);
        // 날짜 넘기기
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDay();
        intent.putExtra("Year", year);
        intent.putExtra("Month", month);
        intent.putExtra("Day", day);
        // 이동
        startActivity(intent);
    }

    // 설정 버튼 만들기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // 설정 눌렀을 때 시간 설정 페이지로 이동
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_btn1:
                Intent intent = new Intent(this, setting_time.class);
                String[] today = makeDayForm();
                intent.putExtra("today", today[1]);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // 이 주의 무지개 보기 버튼 클릭시 페이지 이동
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, weekofRainbow.class);

        String[] param = makeDayForm();
        intent.putExtra("previous", param[0]);
        intent.putExtra("today", param[1]);

        startActivity(intent);
    }

    //날짜 'yyyy-mm-dd'형태로 출력
    public String[] makeDayForm() {
        Calendar cal = getInstance();
        int year = cal.get(YEAR);
        int month = cal.get(MONTH) + 1;
        int day = cal.get(DATE);
        String today = Integer.toString(year);

        String month_s = Integer.toString(month);
        if (month_s.length() == 1)
            month_s = "0" + month;
        String day_s = Integer.toString(day);
        if (day_s.length() == 1)
            day_s = "0" + day;

        today += "-" + month_s + "-" + day_s;

        cal.add(DATE, -28);
        int p_year = cal.get(YEAR);
        int p_month = cal.get(MONTH) + 1;
        int p_day = cal.get(DATE);
        String previous = Integer.toString(p_year);
        String ps_month = Integer.toString(p_month);
        if (ps_month.length() == 1)
            ps_month = "0" + p_month;
        String ps_day = Integer.toString(p_day);
        if (ps_day.length() == 1)
            ps_day = "0" + p_day;

        previous += "-" + ps_month + "-" + ps_day;


        String[] arrtoday = {previous, today};
        return arrtoday;
    }


}