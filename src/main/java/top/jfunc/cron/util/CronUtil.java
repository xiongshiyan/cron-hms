package top.jfunc.cron.util;

import top.jfunc.cron.pojo.CronField;
import top.jfunc.cron.pojo.CronPosition;
import top.jfunc.cron.pojo.HMS;

import java.util.*;

/**
 * 解析cron表达式，计算某天的那些时刻执行。
 * 思路：1、切割cron表达式
 * 2、转换每个域
 * 3、计算执行时间点（关键算法，解析cron表达式）
 * 4、计算某一天的哪些时间点执行
 *
 * @author xiongshiyan at 2018/11/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CronUtil {
    private static final String STAR = "*";
    private static final String COMMA = ",";
    private static final String HYPHEN = "-";
    private static final String SLASH = "/";
    private static final int CRON_LEN = 6;
    private static final int CRON_LEN_YEAR = 7;
    private static final String CRON_CUT = "\\s+";

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
    public static List<HMS> calculate(String cron, Date date) {
        List<CronField> cronFields = convertCronField(cron);
        int year = DateUtil.year(date);
        int week = DateUtil.week(date);
        int month = DateUtil.month(date);
        int day = DateUtil.day(date);
        /// 如果包含年域
        if (CRON_LEN_YEAR == cronFields.size()) {
            CronField fieldYear = cronFields.get(CronPosition.YEAR.getPosition());
            if (!assertExecute(year, fieldYear, calculatePoint(fieldYear))) {
                return Collections.emptyList();
            }
        }
        ///
        /*//检查星期域是否应该执行
        assertExecute(week, cronFields.get(CronPosition.WEEK.getPosition()));
        //检查月域是否应该执行
        assertExecute(month, cronFields.get(CronPosition.MONTH.getPosition()));
        //检查日域是否应该执行
        assertExecute(day, cronFields.get(CronPosition.DAY.getPosition()));*/


        CronField fieldWeek = cronFields.get(CronPosition.WEEK.getPosition());
        List<Integer> listWeek = calculatePoint(fieldWeek);
        CronField fieldMonth = cronFields.get(CronPosition.MONTH.getPosition());
        List<Integer> listMonth = calculatePoint(fieldMonth);
        CronField fieldDay = cronFields.get(CronPosition.DAY.getPosition());
        List<Integer> listDay = calculatePoint(fieldDay);
        ///今天不执行就直接返回空
        if (!assertExecute(week, fieldWeek, listWeek)
                || !assertExecute(month, fieldMonth, listMonth)
                || !assertExecute(day, fieldDay, listDay)) {
            return Collections.emptyList();
        }

        CronField fieldHour = cronFields.get(CronPosition.HOUR.getPosition());
        List<Integer> listHour = calculatePoint(fieldHour);
        CronField fieldMinute = cronFields.get(CronPosition.MINUTE.getPosition());
        List<Integer> listMinute = calculatePoint(fieldMinute);
        CronField fieldSecond = cronFields.get(CronPosition.SECOND.getPosition());
        List<Integer> listSecond = calculatePoint(fieldSecond);

        List<HMS> points = new ArrayList<>(listHour.size() * listMinute.size() * listSecond.size());
        for (Integer hour : listHour) {
            for (Integer minute : listMinute) {
                for (Integer second : listSecond) {
                    points.add(new HMS(hour, minute, second));
                }
            }
        }
        return points;
    }

    private static boolean assertExecute(int num, CronField cronField, List<Integer> list) {
        return STAR.equals(cronField.getExpress()) || numInList(num, list);
    }


    private static boolean numInList(int num, List<Integer> list) {
        for (Integer tmp : list) {
            if (tmp == num) {
                //相同要执行
                return true;
            }
        }
        return false;
    }

    /**
     * 3.计算某域的哪些点
     *
     * @param cronField cron域
     */
    public static List<Integer> calculatePoint(CronField cronField) {
        List<Integer> list = new ArrayList<>(5);
        String express = cronField.getExpress();
        CronPosition cronPosition = cronField.getCronPosition();
        Integer min = cronPosition.getMin();
        Integer max = cronPosition.getMax();

        // *这种情况
        if (STAR.equals(express)) {
            for (int i = min; i <= max; i++) {
                list.add(i);
            }
            return list;
        }
        // 带有,的情况,分割之后每部分单独处理
        if (express.contains(COMMA)) {
            String[] split = express.split(COMMA);
            for (String part : split) {
                list.addAll(calculatePoint(
                        new CronField(cronField.getCronPosition(), part)
                ));
            }
            if (list.size() > 1) {
                //去重
                removeDuplicate(list);
                //排序
                Collections.sort(list);
            }

            return list;
        }
        // 0-3 0/2 3-15/2 5  模式统一为 (min-max)/step
        Integer left;
        Integer right;
        Integer step = 1;

        //包含-的情况
        if (express.contains(HYPHEN)) {
            String[] strings = express.split(HYPHEN);
            left = Integer.valueOf(strings[0]);
            assertRange(cronPosition, left);
            //1-32/2的情况
            if (strings[1].contains(SLASH)) {
                String[] split = strings[1].split(SLASH);
                //32
                right = Integer.valueOf(split[0]);
                assertSize(left, right);
                assertRange(cronPosition, right);
                //2
                step = Integer.valueOf(split[1]);
            } else {
                //1-32的情况
                right = Integer.valueOf(strings[1]);
                assertSize(left, right);
                assertRange(cronPosition, right);
            }
            //仅仅包含/
        } else if (express.contains(SLASH)) {
            String[] strings = express.split(SLASH);
            left = Integer.valueOf(strings[0]);
            assertRange(cronPosition, left);
            step = Integer.valueOf(strings[1]);
            right = max;
            assertSize(left, right);
        } else {
            // 普通的数字
            Integer single = Integer.valueOf(express);
            assertRange(cronPosition, single);
            list.add(single);
            return list;
        }

        for (int i = left; i <= right; i += step) {
            list.add(i);
        }
        return list;

    }

    /**
     * 比较大小,左边的必须比右边小
     */
    private static void assertSize(Integer left, Integer right) {
        if (left > right) {
            throw new IllegalArgumentException("right should bigger than left , but find " + left + " > " + right);
        }
    }

    /**
     * 某个域的范围
     */
    private static void assertRange(CronPosition cronPosition, Integer value) {
        Integer min = cronPosition.getMin();
        Integer max = cronPosition.getMax();
        if (value < min || value > max) {
            throw new IllegalArgumentException(cronPosition.name() + " 域[" + min + " , " + max + "],  but find " + value);
        }
    }

    /**
     * 列表去重
     */
    private static void removeDuplicate(Collection<Integer> list) {
        LinkedHashSet<Integer> set = new LinkedHashSet<>(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
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
        List<String> shaping = new ArrayList<>(split.length);
        shaping.addAll(Arrays.asList(split));
        return shaping;
    }
}
