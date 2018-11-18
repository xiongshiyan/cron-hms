package top.jfunc.cron.pojo;

/**
 * cron表达式某个位置上的一些常量
 * @author xiongshiyan at 2018/11/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public enum  CronPosition {
    SECOND(0 , 0 , 59) ,
    MINUTE(1 , 0 , 59) ,
    HOUR  (2 , 0 , 23) ,
    DAY   (3 , 1 , 31) ,
    MONTH (4 , 1 , 12) ,
    WEEK  (5 , 0 , 6)  ,
    YEAR  (6 , 2018 , 2099);
    /**
     * 在cron表达式中的位置
     */
    private int position;
    /**
     * 该域最小值
     */
    private Integer min;
    /**
     * 该域最大值
     */
    private Integer max;

    CronPosition(int position , Integer min , Integer max){
        this.position = position;
        this.min = min;
        this.max = max;
    }

    public int getPosition() {
        return position;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public static CronPosition fromPosition(int position){
        CronPosition[] values = CronPosition.values();
        for (CronPosition field : values) {
            if(position == field.position){
                return field;
            }
        }
        return null;
    }
}
