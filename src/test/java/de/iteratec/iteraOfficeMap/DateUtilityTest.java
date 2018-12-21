package de.iteratec.iteraOfficeMap;

import de.iteratec.iteraOfficeMap.utility.DateUtility;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilityTest {

    @Test
    void summerTimeTest() {
        Date date = new Date(1526342400000L);

        //Tue May 15 2018 00:00:00 GMT+0200
        Long expectedStart = 1526335200000L;
        //Mon may 15 2018 23:59:59 GMT+0100
        Long expectedEnd = 1526421599999L;

        Long startOfDay = DateUtility.startOfDay(date);
        Long endOfDay = DateUtility.endOfDay(date);

        assertEquals(expectedStart, startOfDay);
        assertEquals(expectedEnd, endOfDay);
    }

    @Test
    void winterTimeTest() {
        //Sat Dec 15 2018 01:00:00 GMT+0100
        Long winterTime = 1544832000000L;
        //Sat Dec 15 2018 00:00:00:000 GMT+0100
        Long expectedEndTime = 1544914799999L;
        //Sat Dec 15 2018 23:59:59:999 GMT+0100
        Long expectedStartTime = 1544828400000L;

        Date date = new Date(winterTime);
        Long startOfDay = DateUtility.startOfDay(date);
        Long endOfDay = DateUtility.endOfDay(date);

        assertEquals(expectedStartTime, startOfDay);
        assertEquals(expectedEndTime, endOfDay);
    }

}
