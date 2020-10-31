package com.nsc.covidscore;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConverterUniTest {
    @Test
    public void dateToTimestampTest() {
        new Converters();
        Calendar calendar = Calendar.getInstance();
        assertNull(Converters.dateToTimestamp(null));
        assertEquals((Long) calendar.getTime().getTime(), Converters.dateToTimestamp(calendar));
    }
}
