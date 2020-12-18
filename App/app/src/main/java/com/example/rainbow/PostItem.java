package com.example.rainbow;

import com.google.gson.annotations.SerializedName;

public class PostItem {
    @SerializedName("target") private int target;

    public int getTarget()
    {
        return target;
    }
    public void setTarget(int target)
    {
        this.target = target;
    }
}
