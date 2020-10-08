package com.nsc.covidscore;

import androidx.room.TypeConverter;

import java.util.Calendar;

public class Converters {

    @TypeConverter
    public static Calendar dateFromTimestamp(Long value) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(value);
        return c;
    }

    @TypeConverter
    public static Long dateToTimestamp(Calendar c) {
        return c == null ? null : c.getTime().getTime();
    }
}
