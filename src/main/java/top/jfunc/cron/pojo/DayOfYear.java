package top.jfunc.cron.pojo;

import java.util.Calendar;

/**
 * 保存日月年
 * @author xiongshiyan at 2018/11/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public final class DayOfYear implements Comparable<DayOfYear> {
    private Integer day;
    private Integer month;
    private Integer year;

    public DayOfYear(Integer day, Integer month , Integer year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayOfYear dayOfYear = (DayOfYear) o;

        if (day != null ? !day.equals(dayOfYear.day) : dayOfYear.day != null) return false;
        if (month != null ? !month.equals(dayOfYear.month) : dayOfYear.month != null) return false;
        return year != null ? year.equals(dayOfYear.year) : dayOfYear.year == null;
    }

    @Override
    public int hashCode() {
        int result = day != null ? day.hashCode() : 0;
        result = 31 * result + (month != null ? month.hashCode() : 0);
        result = 31 * result + (year != null ? year.hashCode() : 0);
        return result;
    }

    /**
     * 计算星期
     */
    public int week(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR , getYear());
        calendar.set(Calendar.DAY_OF_MONTH , getDay());
        calendar.set(Calendar.MONTH , getMonth() - 1);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }


    /**
     * 按照年月日的顺序逐个比较
     */
    @Override
    public int compareTo(DayOfYear o) {
        if (this.getYear() > o.getYear()) {
            return 1;
        }
        if (this.getYear() < o.getYear()) {
            return -1;
        }
        if (this.getMonth() > o.getMonth()) {
            return 1;
        }
        if (this.getMonth() < o.getMonth()) {
            return -1;
        }
        if (this.getDay() > o.getDay()) {
            return 1;
        }
        if (this.getDay() < o.getDay()) {
            return -1;
        }
        return 0;
    }
}
