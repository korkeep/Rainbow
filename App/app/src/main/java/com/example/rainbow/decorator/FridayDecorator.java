package com.example.rainbow.decorator;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;

public class FridayDecorator implements DayViewDecorator {
    private final Calendar calendar = Calendar.getInstance();

    public FridayDecorator() {
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.FRIDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {

//        view.addSpan(new DotSpan(15,Color.rgb(12,63,194)));
        view.addSpan(new DotSpan(15,Color.rgb(71,129,223)));
    }
}