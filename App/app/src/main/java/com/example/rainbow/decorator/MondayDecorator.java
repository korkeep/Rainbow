package com.example.rainbow.decorator;

import android.graphics.Color;
import android.os.Build;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.RequiresApi;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;

public class MondayDecorator implements DayViewDecorator {
    private final Calendar calendar = Calendar.getInstance();

    public MondayDecorator() {
    }

    public MondayDecorator(CalendarDay day) {

        shouldDecorate(day);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.MONDAY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(15, Color.rgb(255,128,0)));
    }
}