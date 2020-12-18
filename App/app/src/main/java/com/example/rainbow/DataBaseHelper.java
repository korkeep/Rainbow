package com.example.rainbow;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "rainbow.db";
    private static final int DATABASE_VERSION=1;

    public DataBaseHelper(@Nullable Context context) {
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        //sqLiteDatabase.execSQL("CREATE TABLE STUDY (date String PRIMARY KEY, time int, goal int , achieve int);");
        //sqLiteDatabase.execSQL("CREATE TABLE DETECT (date String, time String, PRIMARY KEY (date, time));");
        //Log.i("[DataBaseHelper: onCreate]","테이블을 불러옵니다.");
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
    }

    // 메인 달력 화면
    public String[] getNotAchievedDays()
    {
        SQLiteDatabase db =getReadableDatabase();
        String[] result;

        Cursor cursor = db.rawQuery("SELECT date FROM STUDY WHERE achieve = 0",null);
        result = new String[cursor.getCount()];
        int i= 0;
        while(cursor.moveToNext()) {
            result[i] = cursor.getString(0);
            i++;
        }
        db.close();

        return result;
    }

    // 총 공부 시간
    public int getStudyTime(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT time FROM STUDY WHERE date = ?", new String[]{date});

        int result = 0;
        while(cursor.moveToNext())
        {
            result = cursor.getInt(0);
        }
        db.close();
        return result;
    }

    // 목표 공부 시간
    public int getStudyGoal(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT goal FROM STUDY WHERE date = ?", new String[]{date});

        int result = 0;
        while(cursor.moveToNext())
        {
            result = cursor.getInt(0);
        }
        db.close();
        return result;
    }

    // 다른 행동 감지 횟수
    public int getDetectNum(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(d.time) FROM DETECT as d, STUDY as s WHERE d.date = s.date AND (s.date = ?) ", new String[]{date});

        int count= 0;
        while(cursor.moveToNext())
            count = cursor.getInt(0);
        db.close();
        return count;
    }

    // 세팅화면 목표시간
    public int getSettingTime()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT goal FROM STUDY WHERE achieve is NULL", null);
        int goal = 0;
        while(cursor.moveToNext())
            goal = cursor.getInt(0);
        db.close();
        return goal;
    }

    // 세팅화면 목표시간 수정
    public void updateSettingTime(int time)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("UPDATE STUDY SET goal = '" + time + "' WHERE achieve is NULL;");
        db.close();
    }

    // 주간 평
    public int[] getWeekAchieve()
    {
        SQLiteDatabase db = getReadableDatabase();
        String input = "SUN";
        Cursor cursor = db.rawQuery("SELECT achieve FROM STUDY WHERE  achieve IS NOT NULL and date >= " +
                "(SELECT date FROM STUDY WHERE dayofweek = ? LIMIT 1) ", new String[]{input});

        int count = cursor.getCount()/7;
        int[] result = new int[count];
        int resultcount = 0;
        int i= 0;
        int sum = 0;
        while(cursor.moveToNext()) {
            sum += cursor.getInt(0);
            i++;
            if(i%7 == 0)
            {
                if(sum == 7)
                    result[resultcount]=2;
                else if(sum >3 && sum<7)
                    result[resultcount]=1;
                else
                    result[resultcount]=0;
                sum =0;
                resultcount++;
            }
        }
        db.close();
        return result;

    }
}