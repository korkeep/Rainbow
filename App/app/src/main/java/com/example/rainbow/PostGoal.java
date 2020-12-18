package com.example.rainbow;

import com.google.gson.annotations.SerializedName;

public class PostGoal {

    private String date;
    private int time;

    public String getDate()
    {
        return date;
    }
    public int getTime()
    {
        return time;
    }

    public void setDate(String date)
    {
        this.date = date;
    }
    public void setTime(int time)
    {
        this.time = time;
    }

}
