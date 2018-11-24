package top.jfunc.cron.parser;

import top.jfunc.cron.pojo.CronField;
import top.jfunc.cron.pojo.CronPosition;
import top.jfunc.cron.pojo.DayOfYear;
import top.jfunc.cron.pojo.TimeOfDay;
import top.jfunc.cron.util.CompareUtil;
import top.jfunc.cron.util.CronUtil;
import top.jfunc.cron.util.DateUtil;

import java.util.*;

/**
 * @author xiongshiyan at 2018/11/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DayBasedCronParser implements CronParser{
    private static final int CRON_LEN_YEAR = 7;
    private static final int MAX_ADD_YEAR  = 10;
    private final String expression;
    private final TimeZone timeZone;

    private List<CronField> cronFields = null;
    private CronField fieldSecond = null;
    private CronField fieldMinute = null;
    private CronField fieldHour   = null;
    private CronField fieldDay    = null;
    private CronField fieldMonth  = null;
    private CronField fieldWeek   = null;
    private CronField fieldYear   = null;


    public DayBasedCronParser(String expression) {
        this(expression, TimeZone.getDefault());
    }
    public DayBasedCronParser(String expression, TimeZone timeZone) {
        this.expression = expression;
        this.timeZone = timeZone;
        init();
    }

    private void init(){
        cronFields = CronUtil.convertCronField(expression);

        fieldSecond = cronFields.get(CronPosition.SECOND.getPosition());
        fieldMinute = cronFields.get(CronPosition.MINUTE.getPosition());
        fieldHour   = cronFields.get(CronPosition.HOUR.getPosition());
        fieldDay    = cronFields.get(CronPosition.DAY.getPosition());
        fieldMonth  = cronFields.get(CronPosition.MONTH.getPosition());
        fieldWeek   = cronFields.get(CronPosition.WEEK.getPosition());
        fieldSecond.points();
        fieldMinute.points();
        fieldHour.points();
        fieldDay.points();
        fieldMonth.points();
        fieldWeek.points();

    }

    /**
     思路：  1、找到所有时分秒的组合并按照时分秒排序
     *      2、给定的时分秒在以上集合之前、之后处理
     *      3、给定时时分秒在以上集合中找到一个最小的位置
     *      4、day+1循环直到找到满足月、星期的那一天
     *      5、或者在列表中找到最小的即可
     */
    @Override
    public Date next(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(timeZone);
        //基准线,至少从下一秒开始
        calendar.setTime(date);
        calendar.add(Calendar.SECOND , 1);

        /// 如果包含年域
        if (CRON_LEN_YEAR == cronFields.size()) {
            Integer year = DateUtil.year(calendar);
            fieldYear = cronFields.get(CronPosition.YEAR.getPosition());
            List<Integer> listYear = fieldYear.points();
            Integer calYear = CompareUtil.findNext(year, listYear);
            if (!year.equals(calYear)) {
                calendar.set(Calendar.YEAR, calYear);
            }
        }
        return doNext(calendar);
    }
    private Date doNext(Calendar calendar) {
        //////////////////////////////////时分秒///////////////////////////////
        TimeOfDay timeOfDayMin = doTimeOfDay(calendar);

        //////////////////////////////////日月周///////////////////////////////
        return doDayOfYear(0 , calendar , timeOfDayMin);
    }

    /**
     * 处理日月周
     */
    private Date doDayOfYear(int addYear , Calendar calendar, TimeOfDay timeOfDayMin) {
        if(addYear >= MAX_ADD_YEAR){
            throw new IllegalArgumentException("Invalid cron expression【日月周年】 which led to runaway search for next trigger");
        }

        int year = DateUtil.year(calendar);
        //有年域的情况
        if(null != fieldYear){
            //这一年不满足加一年,时分秒日月都重置为最小的
            if(!CronUtil.satisfy(year , fieldYear)){
                CronUtil.addOneYear(calendar, timeOfDayMin);
                return doDayOfYear(++addYear , calendar, timeOfDayMin);
            }

        }
        //先确定日月
        Integer dayNow    = DateUtil.day(calendar);
        Integer monthNow  = DateUtil.month(calendar);


        //可用的日月
        List<Integer> listDay   = fieldDay.points();
        List<Integer> listMonth = fieldMonth.points();


        DayOfYear dayOfYearNow = new DayOfYear(dayNow , monthNow , year);
        //找到最小的一个满足日月星期的
        DayOfYear dayOfYearMin = CronUtil.findMinDayOfYear(dayOfYearNow, listDay, listMonth, fieldWeek);

        //这一年不满足加一年,时分秒日月都重置为最小的
        if(null == dayOfYearMin){
            CronUtil.addOneYear(calendar , timeOfDayMin);
            return doDayOfYear(++addYear , calendar, timeOfDayMin);
        }

        //小于最小的
        if (dayOfYearNow.compareTo(dayOfYearMin) < 0) {
            CronUtil.setDayOfYear(calendar, dayOfYearMin);
            CronUtil.setTimeOfDay(calendar , timeOfDayMin);
        }else {
            //最小的即是要找的day，因为前面一个方法已经处理好时分秒了
            CronUtil.setDayOfYear(calendar , dayOfYearMin);
        }
        return calendar.getTime();
    }

    /**
     * 处理时分秒，并返回最小的时分秒
     */
    private TimeOfDay doTimeOfDay(Calendar calendar) {
        //先确定时分秒
        Integer hourNow    = DateUtil.hour(calendar);
        Integer minuteNow  = DateUtil.minute(calendar);
        Integer secondNow  = DateUtil.second(calendar);


        //找到所有时分秒的组合
        List<TimeOfDay> points = CronUtil.allTimeOfDays(fieldHour, fieldMinute, fieldSecond);

        TimeOfDay timeOfDayNow   = new TimeOfDay(hourNow, minuteNow, secondNow);
        TimeOfDay timeOfDayMin   = points.get(0);
        TimeOfDay timeOfDayMax   = points.get(points.size() - 1);
        //小于最小的
        if (timeOfDayNow.compareTo(timeOfDayMin) < 0) {
            CronUtil.setTimeOfDay(calendar, timeOfDayMin);
            //大于最大的
        } else if (timeOfDayNow.compareTo(timeOfDayMax) > 0) {
            CronUtil.setTimeOfDay(calendar, timeOfDayMin);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        } else {
            TimeOfDay next = CompareUtil.findNext(timeOfDayNow, points);
            CronUtil.setTimeOfDay(calendar, next);
        }
        return timeOfDayMin;
    }

    /**
     * 思路：1、切割cron表达式
     *      2、转换每个域
     *      3、计算执行时间点（关键算法，解析cron表达式）
     *      4、计算某一天的哪些时间点执行
     */
    @Override
    public List<TimeOfDay> timeOfDays(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year  = DateUtil.year(calendar);
        int week  = DateUtil.week(calendar);
        int month = DateUtil.month(calendar);
        int day   = DateUtil.day(calendar);
        /// 如果包含年域
        if (CRON_LEN_YEAR == cronFields.size()) {
            CronField fieldYear = cronFields.get(CronPosition.YEAR.getPosition());
            if (!CronUtil.satisfy(year, fieldYear)) {
                return Collections.emptyList();
            }
        }

        ///今天不执行就直接返回空
        if (!CronUtil.satisfy(week, fieldWeek)
                || !CronUtil.satisfy(month, fieldMonth)
                || !CronUtil.satisfy(day, fieldDay)) {
            return Collections.emptyList();
        }

        CronField fieldHour      = cronFields.get(CronPosition.HOUR.getPosition());
        CronField fieldMinute    = cronFields.get(CronPosition.MINUTE.getPosition());
        CronField fieldSecond    = cronFields.get(CronPosition.SECOND.getPosition());

        return CronUtil.allTimeOfDays(fieldHour, fieldMinute, fieldSecond);
    }
}
