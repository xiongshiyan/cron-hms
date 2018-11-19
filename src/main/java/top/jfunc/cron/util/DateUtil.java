package top.jfunc.cron.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author xiongshiyan at 2018/11/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DateUtil {
    public static final String SDF_DATETIME       = "yyyy-MM-dd HH:mm:ss";
    public static final String SDF_DATETIME_SHORT = "yyyyMMddHHmmss";
    public static final String SDF_DATETIME_MS    = "yyyyMMddHHmmssSSS";
    public static final String SDF_DATE           = "yyyy-MM-dd";

    /**
     * 字符串转日期
     * @param dateStr 日期字符串
     * @return 日期 yyyy-MM-dd HH:mm:ss
     */
    public static Date toDate(String dateStr) {
        return toDate(dateStr, null);
    }

    /**
     * 日期转字符串
     * @param date 日期
     * @return 字符串 yyyy-MM-dd HH:mm:ss
     */
    public static String toStr(Date date) {
        return toStr(date, SDF_DATETIME);
    }

    /**
     * 日期转字符串
     * @param date 日期
     * @param format 格式化字符串
     * @return 字符串
     */
    public static String toStr(Date date, String format) {
        SimpleDateFormat sdf = null;
        if (null != format && !"".equals(format)) {
            sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } else {
            sdf = new SimpleDateFormat(SDF_DATETIME);
            return sdf.format(date);
        }
    }

    /**
     * 字符串转日期
     * @param dateStr 日期字符串
     * @param pattern 格式化字符串
     * @return 日期
     */
    public static Date toDate(String dateStr, String pattern) {
        try {
            if (null != pattern && !"".equals(pattern)) {
                return new SimpleDateFormat(pattern).parse(dateStr);
            } else {
                return new SimpleDateFormat(SDF_DATETIME).parse(dateStr);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 计算某一天是一个月的哪一天
     * @param date 日期
     * @return 1-31
     */
    public static int day(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
    /**
     * 计算某一天是星期几
     * @param date 日期
     * @return 星期几,星期1是1,星期天是0  0-6
     */
    public static int week(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }
    /**
     * 计算某一天的月份
     * @param date 日期
     * @return 月份,1开始
     */
    public static int month(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }
    /**
     * 计算某一天的年
     * @param date 日期
     * @return 年
     */
    public static int year(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }
    /**
     * 计算某一天的时
     * @param date 日期
     * @return 时 0-23
     */
    public static int hour(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }
    /**
     * 计算某一天的分
     * @param date 日期
     * @return 秒 0-59
     */
    public static int minute(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }
    /**
     * 计算某一天的秒
     * @param date 日期
     * @return 秒 0-59
     */
    public static int second(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.SECOND);
    }
}
