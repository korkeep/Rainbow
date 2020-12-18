package com.example.rainbow.decorator;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;

public class SundayDecorator implements DayViewDecorator {
    private final Calendar calendar = Calendar.getInstance();
    ArrayList<CalendarDay> dates;

    public SundayDecorator() {
    }

    public SundayDecorator(ArrayList<CalendarDay> dates){
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        /*
        boolean ishere = false;
        for(int i=0;i<dates.size();i++)
        {
            if (day.getDay() == dates.indexOf(i)) {
                ishere = true;
            }
        }*/
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.SUNDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.RED));
        view.addSpan(new DotSpan(15,Color.rgb(223,71,74)));
    }
}
