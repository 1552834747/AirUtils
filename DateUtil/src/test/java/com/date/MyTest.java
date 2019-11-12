package com.date;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class MyTest {

    private static String style = "yyyy/MM/dd HH:mm:ss";

    @Test
    public void test0() throws ParseException {
        //2019/11/12 16:36:00
        String dateStr = "2019/11/12 16:36:00";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long timeZone = getTimeZone(dateStr, DateUtil.ZoneKey.JST, style);
        System.out.println(timeZone);
        System.out.println(new Date(timeZone));
        System.out.println(getZoneTime(timeZone, DateUtil.ZoneKey.CTT, style));
    }

    @Test
    public void test2(){
        System.out.println(getZoneTime(1573547760000L, DateUtil.ZoneKey.CTT, "yyyy/MM/dd HH:mm:ss"));
        System.out.println(getZoneTime(1573547760000L, DateUtil.ZoneKey.JST, "yyyy/MM/dd HH:mm:ss"));
    }


    public String getZoneTime(long time, DateUtil.ZoneKey zone, String style){
        SimpleDateFormat format = new SimpleDateFormat(style);
        format.setTimeZone(TimeZone.getTimeZone(ZoneId.SHORT_IDS.get(zone.name())));
        return format.format(new Date(time));
    }

    public long getTimeZone(String dateStr, DateUtil.ZoneKey zone,String style){
        SimpleDateFormat format = new SimpleDateFormat(style);
        format.setTimeZone(TimeZone.getTimeZone(ZoneId.SHORT_IDS.get(zone.name())));
        try {
            Date parse = format.parse(dateStr);
            return parse.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
