package top.jfunc.cron.util;

import top.jfunc.cron.pojo.CronField;
import top.jfunc.cron.pojo.CronPosition;
import top.jfunc.cron.pojo.HMS;

import java.util.*;

/**
 * @author xiongshiyan at 2018/11/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CronUtil {
    private static final String STAR      = "*";
    private static final String COMMA     = ",";
    private static final String HYPHEN    = "-";
    private static final String SLASH     = "/";
    private static final int CRON_LEN     = 6;
    private static final String CRON_CUT  = "\\s+";
    /**
     * 计算cron表达式在某一天的那些时间执行,精确到秒
     * 秒 分 时 日 月 周 (年)
      "0 15 10 ? * *"  每天上午10:15触发
      "0 0/5 14 * * ?"  在每天下午2点到下午2:55期间的每5分钟触发
      "0 0-5 14 * * ?"  每天下午2点到下午2:05期间的每1分钟触发
      "0 10,44 14 ? 3 WED"  三月的星期三的下午2:10和2:44触发
      "0 15 10 ? * MON-FRI"  周一至周五的上午10:15触发
     ---------------------
     * @param cron cron表达式
     * @param date 时间,某天
     * @return 这一天的哪些时分秒执行,不执行的返回空
     */
    public static List<HMS> calculate(String cron , Date date) {
        List<CronField> cronFields = convertCronField(cron);
        int week = DateUtil.week(date);
        int month = DateUtil.month(date);
        int day = DateUtil.day(date);
        ///
        /*//检查星期域是否应该执行
        assertExecute(week, cronFields.get(CronPosition.WEEK.getPosition()));
        //检查月域是否应该执行
        assertExecute(month, cronFields.get(CronPosition.MONTH.getPosition()));
        //检查日域是否应该执行
        assertExecute(day, cronFields.get(CronPosition.DAY.getPosition()));*/

        ///今天不执行就直接返回空
        if(!assertExecute(week , cronFields.get(CronPosition.WEEK.getPosition()))
           ||!assertExecute(month , cronFields.get(CronPosition.MONTH.getPosition()))
           ||!assertExecute(day , cronFields.get(CronPosition.DAY.getPosition()))){
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
                    points.add(new HMS(hour , minute , second));
                }
            }
        }
        return points;
    }

    /*private static void assertExecute(int field, CronField cronField) throws NotExecuteException {
        if(!STAR.equals(cronField.getExpress())){
            //计算出来星期几要执行
            List<Integer> list = calculatePoint(cronField);
            if(!numInList(field, list)){
                throw new NotExecuteException(cronField.getCronPosition().name() + " " + field + " 不执行");
            }
        }
    }*/

    private static boolean assertExecute(int num, CronField cronField) {
        if(STAR.equals(cronField.getExpress())){
            return true;
        }
        //计算出来星期几几几要执行
        List<Integer> list = calculatePoint(cronField);
        return numInList(num, list);
    }


    private static boolean numInList(int num, List<Integer> list) {
        for (Integer tmp : list) {
            if(tmp == num){
                //星期相同要执行
                return true;
            }
        }
        return false;
    }

    /**
     * 计算某域的哪些点
     * @param cronField cron域
     */
    public static List<Integer> calculatePoint(CronField cronField){
        List<Integer> list = new ArrayList<>();
        String express = cronField.getExpress();
        CronPosition cronPosition = cronField.getCronPosition();
        Integer min = cronPosition.getMin();
        Integer max = cronPosition.getMax();

        // *这种情况
        if(STAR.equals(express)){
            for (int i = min; i <= max; i++) {
                list.add(i);
            }
            return list;
        }
        // 带有,的情况
        if(express.contains(COMMA)){
            String[] split = express.split(COMMA);
            for (int i = 0; i < split.length; i++) {
                String part = split[i];

                //1-4,5 这种情况
                if(part.contains(HYPHEN)){
                    String[] strings = part.split(HYPHEN);
                    Integer left = Integer.valueOf(strings[0]);
                    Integer right = Integer.valueOf(strings[1]);
                    if(left > right){
                        throw new IllegalArgumentException("right should more than left");
                    }
                    for (int j = left; j <= right ; j++) {
                        if(j < min || j > max){
                            throw new IllegalArgumentException(cronPosition.name() + " 域[" + min + " , " + max + "]");
                        }
                        list.add(j);
                    }
                }

                //普通的4,5
                Integer s = Integer.valueOf(part);
                if(s < min || s > max){
                    throw new IllegalArgumentException(cronPosition.name() + " 域[" + min + " , " + max + "]");
                }
                list.add(s);
            }
            return list;
        }
        // 0-3 0/2 3-15/2   模式统一为 min-max/step
        Integer left = min;
        Integer right = max;
        Integer step = 1;

        //包含-的情况
        if(express.contains(HYPHEN)){
            String[] strings = express.split(HYPHEN);
            left = Integer.valueOf(strings[0]);
            //1-32/2的情况
            if(strings[1].contains(SLASH)){
                String[] split = strings[1].split(SLASH);
                //32
                right = Integer.valueOf(split[0]);
                if(left > right){
                    throw new IllegalArgumentException("right should more than left");
                }
                //2
                step = Integer.valueOf(split[1]);
            }else {
                //1-32的情况
                right = Integer.valueOf(strings[1]);
                if(left > right){
                    throw new IllegalArgumentException("right should more than left");
                }
            }
            //仅仅包含/
        }else if(express.contains(SLASH)){
            String[] strings = express.split(SLASH);
            left = Integer.valueOf(strings[0]);
            step = Integer.valueOf(strings[1]);
            right = max;
            if(left > right){
                throw new IllegalArgumentException("right should more than left");
            }
        }else {
            // 普通的数字
            list.add(Integer.valueOf(express));
            return list;
        }

        for (int i = left; i <= right ; i+=step) {
            list.add(i);
        }
        return list;

    }

    /**
     * cron表达式转换为域
     */
    public static List<CronField> convertCronField(String cron){
        List<String> cut = cut(cron);
        int size = cut.size();
        if(CRON_LEN != size){
            throw new IllegalArgumentException("cron 表达式必须有六个域");
        }
        List<CronField> cronFields = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            cronFields.add(new CronField(
                    CronPosition.fromPosition(i) ,
                    cut.get(i)));
        }
        return cronFields;
    }

    /**
     * 把cron表达式切成域
     * @param cron cron
     * @return 代表每个域的列表
     */
    public static List<String> cut(String cron){
        cron = cron.trim();
        String[] split = cron.split(CRON_CUT);
        List<String> shaping = new ArrayList<>(split.length);
        shaping.addAll(Arrays.asList(split));
        return shaping;
    }
}
