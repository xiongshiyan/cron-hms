package top.jfunc.cron.pojo;

import top.jfunc.cron.util.DateUtil;

import java.util.Calendar;

/**
 * 保存时分秒
 * @author xiongshiyan at 2018/11/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public final class TimeOfDay implements Comparable<TimeOfDay> {
    private Integer hour;
    private Integer minute;
    private Integer second;

    public TimeOfDay(Integer hour, Integer minute, Integer second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "TimeOfDay{" +
                "hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeOfDay timeOfDay = (TimeOfDay) o;

        if (hour != null ? !hour.equals(timeOfDay.hour) : timeOfDay.hour != null) return false;
        if (minute != null ? !minute.equals(timeOfDay.minute) : timeOfDay.minute != null) return false;
        return second != null ? second.equals(timeOfDay.second) : timeOfDay.second == null;
    }

    @Override
    public int hashCode() {
        int result = hour != null ? hour.hashCode() : 0;
        result = 31 * result + (minute != null ? minute.hashCode() : 0);
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    /**
     * 按照时分秒的顺序逐个比较
     */
    @Override
    public int compareTo(TimeOfDay o) {
        if (this.getHour() > o.getHour()) {
            return 1;
        }
        if (this.getHour() < o.getHour()) {
            return -1;
        }
        if (this.getMinute() > o.getMinute()) {
            return 1;
        }
        if (this.getMinute() < o.getMinute()) {
            return -1;
        }
        if (this.getSecond() > o.getSecond()) {
            return 1;
        }
        if (this.getSecond() < o.getSecond()) {
            return -1;
        }
        return 0;
    }

    public boolean equalsWithTolerance(TimeOfDay another , int seconds){
        return DateUtil.equalsWithTolerance(this , another, seconds);
    }
}
