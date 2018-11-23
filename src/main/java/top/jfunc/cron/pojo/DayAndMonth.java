package top.jfunc.cron.pojo;

/**
 * 保存日月
 * @author xiongshiyan at 2018/11/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public final class DayAndMonth implements Comparable<DayAndMonth> {
    private Integer day;
    private Integer month;

    public DayAndMonth(Integer day, Integer month) {
        this.day = day;
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayAndMonth that = (DayAndMonth) o;

        if (day != null ? !day.equals(that.day) : that.day != null) return false;
        return month != null ? month.equals(that.month) : that.month == null;
    }

    @Override
    public int hashCode() {
        int result = day != null ? day.hashCode() : 0;
        result = 31 * result + (month != null ? month.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DayAndMonth{" +
                "day=" + day +
                ", month=" + month +
                '}';
    }

    /**
     * 按照时分秒的顺序逐个比较
     */
    @Override
    public int compareTo(DayAndMonth o) {
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
