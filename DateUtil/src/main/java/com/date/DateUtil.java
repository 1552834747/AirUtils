package com.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    enum ZoneKey {
        ECT,        //欧洲              巴黎（法国首都）
        ACT,        //澳洲              达尔文市
        AET,        //澳洲              悉尼
        MIT,        //太平洋            阿皮亚（西萨摩亚首都）
        NST,        //太平洋            奥克兰（新西兰港市）
        SST,        //太平洋            瓜达康纳尔岛（南太平洋Solomon 群岛的一个岛）
        ART,        //非洲              开罗（埃及首都）
        CAT,        //非洲              哈拉雷（津巴布韦首都）
        EAT,        //非洲              亚的斯亚贝巴（埃塞俄比亚首都）
        BST,        //亚洲              达卡（孟加拉首都）
        CTT,        //亚洲              上海
        IST,        //亚洲              加尔各答（印度）
        JST,        //亚洲              东京
        NET,        //亚洲              耶烈万（亚美尼亚共和国首都，即埃里温）
        PLT,        //亚洲              卡拉奇（巴基斯坦港市）
        VST,        //亚洲              胡志明市（越南）
        AGT,        //美洲              阿根廷                             布宜诺斯艾利斯（阿根廷首都）
        AST,        //美洲              安克雷奇市
        BET,        //美洲              圣保罗
        CNT,        //美洲              圣约翰（安提瓜岛首都）
        CST,        //美洲              芝加哥（美国中西部城市）
        IET,        //美洲              印第安纳州（美国中部的州）         印第安纳波利斯（美国印第安纳州首府）
        PRT,        //美洲              波多黎各（位于西印度群岛东部的岛屿）
        PNT,        //美洲              菲尼克斯
        PST,        //美洲              洛杉矶
        EST,        //(美国)东部标准时间
        MST,        //北美山地标准时间
        HST;        //美国夏威夷时间
    }


    /**
     * 将毫秒值转换为指定时区的时间字符串
     * @param time          时间毫秒值
     * @param zone          时区
     * @param style         转换样式
     * @return
     */
    public String getZoneTime(long time,ZoneKey zone,String style){
        SimpleDateFormat format = new SimpleDateFormat(style);
        format.setTimeZone(TimeZone.getTimeZone(ZoneId.SHORT_IDS.get(zone.name())));
        String date = format.format(new Date(time));
        return date;
    }

    /**
     * 将时间字符串按指定时区转换为毫秒值
     * @param dateStr       字符串类型时间
     * @param zone          时区
     * @param style         转换样式
     * @return
     */
    public long getTimeZone(String dateStr, ZoneKey zone,String style){
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
