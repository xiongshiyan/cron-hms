package top.jfunc.cron.pojo;

import top.jfunc.cron.util.CompareUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * cron表达式的域
 * @author xiongshiyan
 */
public class CronField {
    public static final String STAR = "*";
    public static final String COMMA = ",";
    public static final String HYPHEN = "-";
    public static final String SLASH = "/";

    private CronPosition cronPosition;
    private String express;
    private List<Integer> listCache = null;

    public CronField(CronPosition cronPosition, String express) {
        this.cronPosition = cronPosition;
        this.express = express;
    }

    public CronPosition getCronPosition() {
        return cronPosition;
    }

    public String getExpress() {
        return express;
    }

    /**
     * 是否包含全部的数值
     */
    public boolean containsAll(){
        return STAR.equals(getExpress());
    }

    /**
     * 3.计算某域的哪些点
     */
    public List<Integer> points() {
        //缓存计算的
        if(null != listCache){
            return listCache;
        }

        listCache = new ArrayList<>(5);

        Integer min = cronPosition.getMin();
        Integer max = cronPosition.getMax();

        // *这种情况
        if (STAR.equals(express)) {
            for (int i = min; i <= max; i++) {
                listCache.add(i);
            }
            return listCache;
        }
        // 带有,的情况,分割之后每部分单独处理
        if (express.contains(COMMA)) {
            String[] split = express.split(COMMA);
            for (String part : split) {
                listCache.addAll( new CronField(cronPosition, part).points());
            }
            if (listCache.size() > 1) {
                //去重
                CompareUtil.removeDuplicate(listCache);
                //排序
                Collections.sort(listCache);
            }

            return listCache;
        }
        // 0-3 0/2 3-15/2 5  模式统一为 (min-max)/step
        Integer left;
        Integer right;
        Integer step = 1;

        //包含-的情况
        if (express.contains(HYPHEN)) {
            String[] strings = express.split(HYPHEN);
            left = Integer.valueOf(strings[0]);
            CompareUtil.assertRange(cronPosition, left);
            //1-32/2的情况
            if (strings[1].contains(SLASH)) {
                String[] split = strings[1].split(SLASH);
                //32
                right = Integer.valueOf(split[0]);
                CompareUtil.assertSize(left, right);
                CompareUtil.assertRange(cronPosition, right);
                //2
                step = Integer.valueOf(split[1]);
            } else {
                //1-32的情况
                right = Integer.valueOf(strings[1]);
                CompareUtil.assertSize(left, right);
                CompareUtil.assertRange(cronPosition, right);
            }
            //仅仅包含/
        } else if (express.contains(SLASH)) {
            String[] strings = express.split(SLASH);
            left = Integer.valueOf(strings[0]);
            CompareUtil.assertRange(cronPosition, left);
            step = Integer.valueOf(strings[1]);
            right = max;
            CompareUtil.assertSize(left, right);
        } else {
            // 普通的数字
            Integer single = Integer.valueOf(express);
            //星期域上 7 转换为 0
            if(CronPosition.WEEK == this.cronPosition && 7 == single){
                single = 0;
            }
            CompareUtil.assertRange(cronPosition, single);
            listCache.add(single);
            return listCache;
        }

        for (int i = left; i <= right; i += step) {
            listCache.add(i);
        }
        return listCache;

    }

    @Override
    public String toString() {
        return "CronField{" +
                "cronPosition=" + cronPosition +
                ", express='" + express + '\'' +
                '}';
    }
}
