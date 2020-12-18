package com.example.rainbow.decorator;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;

public class WednesdayDecorator implements DayViewDecorator {
    private final Calendar calendar = Calendar.getInstance();

    public WednesdayDecorator() {
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.WEDNESDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(15,Color.rgb(81,181,47)));
    }
}
