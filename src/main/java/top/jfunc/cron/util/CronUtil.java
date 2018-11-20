package top.jfunc.cron.util;

import top.jfunc.cron.pojo.CronField;
import top.jfunc.cron.pojo.CronPosition;
import top.jfunc.cron.pojo.TimeOfDay;

import java.util.*;

/**
 * 一、根据cron表达式，计算某天的那些时刻执行。
 * 思路：1、切割cron表达式
 *      2、转换每个域
 *      3、计算执行时间点（关键算法，解析cron表达式）
 *      4、计算某一天的哪些时间点执行
 * 二、根据cron表达式，给定Date，计算下一个执行时间点
 * 思路：1、找到所有时分秒的组合并按照时分秒排序
 *      2、给定的时分秒在以上集合之前、之后处理
 *      3、给定时时分秒在以上集合中找到一个最小的位置
 *      4、day+1循环直到找到满足月、星期的那一天
 * {@link https://www.cnblogs.com/junrong624/p/4239517.html}
 * {@link https://www.cnblogs.com/summary-2017/p/8974139.html}
 *
 * @author xiongshiyan at 2018/11/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CronUtil {
    private static final int CRON_LEN = 6;
    private static final int CRON_LEN_YEAR = 7;
    private static final String CRON_CUT = "\\s+";

    private static final int MAX_ADD_COUNT = 366;


    /**
     * 给定cron表达式和日期，计算满足cron的下一个执行时间点
     *
     * @param cron cron表达式
     * @param date 日期时间
     * @return 满足cron的下一个执行时间点
     */
    public static Date next(String cron, Date date) {
        List<CronField> cronFields = convertCronField(cron);
        CronField fieldSecond = cronFields.get(CronPosition.SECOND.getPosition());
        List<Integer> listSecond = fieldSecond.calculatePoint();
        CronField fieldMinute = cronFields.get(CronPosition.MINUTE.getPosition());
        List<Integer> listMinute = fieldMinute.calculatePoint();
        CronField fieldHour = cronFields.get(CronPosition.HOUR.getPosition());
        List<Integer> listHour = fieldHour.calculatePoint();
        CronField fieldDay = cronFields.get(CronPosition.DAY.getPosition());
        List<Integer> listDay = fieldDay.calculatePoint();
        CronField fieldMonth = cronFields.get(CronPosition.MONTH.getPosition());
        List<Integer> listMonth = fieldMonth.calculatePoint();
        CronField fieldWeek = cronFields.get(CronPosition.WEEK.getPosition());
        List<Integer> listWeek = fieldWeek.calculatePoint();

        Calendar calendar = Calendar.getInstance();
        //基准线
        calendar.setTime(date);

        /// 如果包含年域
        if (CRON_LEN_YEAR == cronFields.size()) {
            Integer year = DateUtil.year(date);
            CronField fieldYear = cronFields.get(CronPosition.YEAR.getPosition());
            List<Integer> listYear = fieldYear.calculatePoint();
            Integer calYear = CompareUtil.findNext(year, listYear);
            if (!year.equals(calYear)) {
                calendar.set(Calendar.YEAR, calYear);
            }
        }

        return doNext(calendar, listHour, listMinute, listSecond, listDay, listMonth, listWeek);
    }

    private static Date doNext(Calendar calendar, List<Integer> listHour, List<Integer> listMinute, List<Integer> listSecond, List<Integer> listDay, List<Integer> listMonth, List<Integer> listWeek) {
        Date newDate = calendar.getTime();
        //先确定时分秒
        Integer hourNow = DateUtil.hour(newDate);
        Integer minuteNow = DateUtil.minute(newDate);
        Integer secondNow = DateUtil.second(newDate);

        //找到所有时分秒的组合
        List<TimeOfDay> points = new ArrayList<>(listHour.size() * listMinute.size() * listSecond.size());
        for (Integer hour : listHour) {
            for (Integer minute : listMinute) {
                for (Integer second : listSecond) {
                    points.add(new TimeOfDay(hour, minute, second));
                }
            }
        }
        //排序
        Collections.sort(points);

        TimeOfDay timeOfDayNow = new TimeOfDay(hourNow, minuteNow, secondNow);
        //小于最小的
        TimeOfDay timeOfDayMin = points.get(0);
        TimeOfDay timeOfDayMax = points.get(points.size() - 1);
        if (timeOfDayNow.compareTo(timeOfDayMin) < 0) {
            setHMS(calendar, timeOfDayMin);
            //大于最大的
        } else if (timeOfDayNow.compareTo(timeOfDayMax) > 0) {
            setHMS(calendar, timeOfDayMin);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        } else {
            ///
            /*for (TimeOfDay point : points) {
                //从小到大的列表中找到第一个大于等于某个值的
                if(timeOfDayNow.compareTo(point) <= 0){
                    setHMS(calendar , point);
                    break;
                }
            }*/
            TimeOfDay next = CompareUtil.findNext(timeOfDayNow, points);
            setHMS(calendar, next);
        }

        Integer day = DateUtil.day(calendar.getTime());
        Integer month = DateUtil.month(calendar.getTime());
        Integer week = DateUtil.week(calendar.getTime());

        ///天、月、周必须满足,否则加一天
        int count = 0;
        boolean setting = false;
        while (!CompareUtil.inList(day, listDay)
                || !CompareUtil.inList(month, listMonth)
                || !CompareUtil.inList(week, listWeek)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            day = DateUtil.day(calendar.getTime());
            month = DateUtil.month(calendar.getTime());
            week = DateUtil.week(calendar.getTime());
            //加了一天的情况下,时分秒就可以用最小的了,只需要设置一次
            if (!setting) {
                setHMS(calendar, timeOfDayMin);
                setting = true;
            }
            count++;
            //极端情况下：这尼玛太坑了,一般遇不到:加了一年还未找到
            if (count >= MAX_ADD_COUNT) {
                //break;
                throw new IllegalArgumentException("一年之中都未找到符合要求的时间,请检查您的cron表达式");
            }
        }
        ///其实可以再一天天往下找直到找到为止
        /*if(count >= MAX_ADD_COUNT){
            return next(cron , calendar.getTime());
        }*/
        return calendar.getTime();
    }

    /**
     * 设置时分秒域
     */
    private static void setHMS(Calendar calendar, TimeOfDay timeOfDay) {
        calendar.set(Calendar.HOUR_OF_DAY, timeOfDay.getHour());
        calendar.set(Calendar.MINUTE, timeOfDay.getMinute());
        calendar.set(Calendar.SECOND, timeOfDay.getSecond());
    }


    /**
     * 4.计算cron表达式在某一天的那些时间执行,精确到秒
     * 秒 分 时 日 月 周 (年)
     * "0 15 10 ? * *"  每天上午10:15触发
     * "0 0/5 14 * * ?"  在每天下午2点到下午2:55期间的每5分钟触发
     * "0 0-5 14 * * ?"  每天下午2点到下午2:05期间的每1分钟触发
     * "0 10,44 14 ? 3 WED"  三月的星期三的下午2:10和2:44触发
     * "0 15 10 ? * MON-FRI"  周一至周五的上午10:15触发
     * ---------------------
     *
     * @param cron cron表达式
     * @param date 时间,某天
     * @return 这一天的哪些时分秒执行, 不执行的返回空
     */
    public static List<TimeOfDay> calculate(String cron, Date date) {
        List<CronField> cronFields = convertCronField(cron);
        int year = DateUtil.year(date);
        int week = DateUtil.week(date);
        int month = DateUtil.month(date);
        int day = DateUtil.day(date);
        /// 如果包含年域
        if (CRON_LEN_YEAR == cronFields.size()) {
            CronField fieldYear = cronFields.get(CronPosition.YEAR.getPosition());
            if (!assertExecute(year, fieldYear, fieldYear.calculatePoint())) {
                return Collections.emptyList();
            }
        }

        CronField fieldWeek = cronFields.get(CronPosition.WEEK.getPosition());
        List<Integer> listWeek = fieldWeek.calculatePoint();
        CronField fieldMonth = cronFields.get(CronPosition.MONTH.getPosition());
        List<Integer> listMonth = fieldMonth.calculatePoint();
        CronField fieldDay = cronFields.get(CronPosition.DAY.getPosition());
        List<Integer> listDay = fieldDay.calculatePoint();
        ///今天不执行就直接返回空
        if (!assertExecute(week, fieldWeek, listWeek)
                || !assertExecute(month, fieldMonth, listMonth)
                || !assertExecute(day, fieldDay, listDay)) {
            return Collections.emptyList();
        }

        CronField fieldHour = cronFields.get(CronPosition.HOUR.getPosition());
        List<Integer> listHour = fieldHour.calculatePoint();
        CronField fieldMinute = cronFields.get(CronPosition.MINUTE.getPosition());
        List<Integer> listMinute = fieldMinute.calculatePoint();
        CronField fieldSecond = cronFields.get(CronPosition.SECOND.getPosition());
        List<Integer> listSecond = fieldSecond.calculatePoint();

        List<TimeOfDay> points = new ArrayList<>(listHour.size() * listMinute.size() * listSecond.size());
        for (Integer hour : listHour) {
            for (Integer minute : listMinute) {
                for (Integer second : listSecond) {
                    points.add(new TimeOfDay(hour, minute, second));
                }
            }
        }
        return points;
    }

    private static boolean assertExecute(int num, CronField cronField, List<Integer> list) {
        return CronField.STAR.equals(cronField.getExpress()) || CompareUtil.inList(num, list);
    }

    /**
     * 2.cron域表达式转换为域
     */
    public static List<CronField> convertCronField(String cron) {
        List<String> cut = cut(cron);
        int size = cut.size();
        if (CRON_LEN != size && (CRON_LEN + 1) != size) {
            throw new IllegalArgumentException("cron表达式必须有六个域或者七个域(最后为年)");
        }
        List<CronField> cronFields = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            CronPosition cronPosition = CronPosition.fromPosition(i);
            cronFields.add(new CronField(
                    cronPosition,
                    CronShapingUtil.shaping(cut.get(i), cronPosition)
            ));
        }
        return cronFields;
    }

    /**
     * 1.把cron表达式切成域表达式
     *
     * @param cron cron
     * @return 代表每个域的列表
     */
    public static List<String> cut(String cron) {
        cron = cron.trim();
        String[] split = cron.split(CRON_CUT);
        return Arrays.asList(split);
    }
}
