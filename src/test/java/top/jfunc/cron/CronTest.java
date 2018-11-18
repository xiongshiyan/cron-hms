package top.jfunc.cron;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.cron.pojo.CronField;
import top.jfunc.cron.pojo.HMS;
import top.jfunc.cron.pojo.NotExecuteException;
import top.jfunc.cron.util.CronUtil;
import top.jfunc.cron.util.DateUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author xiongshiyan at 2018/11/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CronTest {
    @Test
    public void testCalculate() {
        Date date = DateUtil.toDate("2018-11-17 12:00:12");
        Assert.assertEquals(2018, DateUtil.year(date));
        Assert.assertEquals(11, DateUtil.month(date));
        Assert.assertEquals(6, DateUtil.week(date));
        Assert.assertEquals(17, DateUtil.day(date));

        date = DateUtil.toDate("2018-11-18 12:00:12");
        Assert.assertEquals(0, DateUtil.week(date));
    }

    @Test
    public void testCut() {
        String cron = "0     15    10   ? *    *   ";
        Assert.assertEquals(
                Arrays.asList("0", "15", "10", "?", "*", "*"),
                CronUtil.cut(cron));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertField() {
        List<CronField> cronFields = CronUtil.convertCronField("0 0-5 14/2 * * ?");
        for (int i = 0; i < 6; i++) {
            CronField field = cronFields.get(i);
            Assert.assertEquals(field.getCronPosition().getPosition(), i);
        }
        Assert.assertEquals("0" , cronFields.get(0).getExpress());
        Assert.assertEquals("0-5" , cronFields.get(1).getExpress());
        Assert.assertEquals("14/2" , cronFields.get(2).getExpress());
        Assert.assertEquals("*" , cronFields.get(3).getExpress());
        Assert.assertEquals("*" , cronFields.get(4).getExpress());
        Assert.assertEquals("*" , cronFields.get(5).getExpress());
        List<CronField> fields = CronUtil.convertCronField("0 15 10 ? JAN-NOV MON-FRI");
        for (int i = 0; i < 6; i++) {
            CronField field = fields.get(i);
            Assert.assertEquals(field.getCronPosition().getPosition(), i);
        }
        Assert.assertEquals("0" , fields.get(0).getExpress());
        Assert.assertEquals("15" , fields.get(1).getExpress());
        Assert.assertEquals("10" , fields.get(2).getExpress());
        Assert.assertEquals("*" , fields.get(3).getExpress());
        Assert.assertEquals("1-11" , fields.get(4).getExpress());
        Assert.assertEquals("1-5" , fields.get(5).getExpress());


        //抛异常 必须六个域
        CronUtil.convertCronField("0 15 10 ? JAN-NOV MON-FRI 2018");
    }
    @Test
    public void testConvertCronField(){
        List<CronField> cronFields = CronUtil.convertCronField("1 0-5 1/3 1,3,4 0-11/2 ?");
        Assert.assertEquals(Collections.singletonList(1) , CronUtil.calculatePoint(cronFields.get(0)));
        Assert.assertEquals(Arrays.asList(0,1,2,3,4,5) , CronUtil.calculatePoint(cronFields.get(1)));
        Assert.assertEquals(Arrays.asList(1,4,7,10,13,16,19,22) , CronUtil.calculatePoint(cronFields.get(2)));
        Assert.assertEquals(Arrays.asList(1,3,4) , CronUtil.calculatePoint(cronFields.get(3)));
        Assert.assertEquals(Arrays.asList(0,2,4,6,8,10) , CronUtil.calculatePoint(cronFields.get(4)));
        Assert.assertEquals(Arrays.asList(0,1,2,3,4,5,6) , CronUtil.calculatePoint(cronFields.get(5)));
    }

    @Test
    public void testCal() throws Exception{
        Date date = DateUtil.toDate("2018-11-18 12:00:12");
        List<HMS> calculate = CronUtil.calculate("0 15 10 ? * *", date);
        Assert.assertEquals(Arrays.asList(new HMS(10 , 15 , 0)) , calculate);

        calculate = CronUtil.calculate("0-5 15 10 ? * *", date);
        Assert.assertEquals(Arrays.asList(
                new HMS(10 , 15 , 0),
                new HMS(10 , 15 , 1),
                new HMS(10 , 15 , 2),
                new HMS(10 , 15 , 3),
                new HMS(10 , 15 , 4),
                new HMS(10 , 15 , 5)) , calculate);

        calculate = CronUtil.calculate("0 1/10 10 ? * *", date);
        Assert.assertEquals(Arrays.asList(
                new HMS(10 , 1 , 0),
                new HMS(10 , 11 , 0),
                new HMS(10 , 21 , 0),
                new HMS(10 , 31 , 0),
                new HMS(10 , 41 , 0),
                new HMS(10 , 51 , 0)) , calculate);

        calculate = CronUtil.calculate("0 1,4,6,8,10,50 10 ? * *", date);
        Assert.assertEquals(Arrays.asList(
                new HMS(10 , 1 , 0),
                new HMS(10 , 4 , 0),
                new HMS(10 , 6 , 0),
                new HMS(10 , 8 , 0),
                new HMS(10 , 10 , 0),
                new HMS(10 , 50 , 0)) , calculate);

        calculate = CronUtil.calculate("0 1-30/5 10 ? * *", date);
        Assert.assertEquals(Arrays.asList(
                new HMS(10 , 1 , 0),
                new HMS(10 , 6 , 0),
                new HMS(10 , 11 , 0),
                new HMS(10 , 16 , 0),
                new HMS(10 , 21 , 0),
                new HMS(10 , 26 , 0)) , calculate);

        calculate = CronUtil.calculate("0 1-30/5 10 ? * SUN", date);
        Assert.assertEquals(Arrays.asList(
                new HMS(10 , 1 , 0),
                new HMS(10 , 6 , 0),
                new HMS(10 , 11 , 0),
                new HMS(10 , 16 , 0),
                new HMS(10 , 21 , 0),
                new HMS(10 , 26 , 0)) , calculate);

        calculate = CronUtil.calculate("0 1-30/5 10 ? 11 *", date);
        Assert.assertEquals(Arrays.asList(
                new HMS(10 , 1 , 0),
                new HMS(10 , 6 , 0),
                new HMS(10 , 11 , 0),
                new HMS(10 , 16 , 0),
                new HMS(10 , 21 , 0),
                new HMS(10 , 26 , 0)) , calculate);

        calculate = CronUtil.calculate("0 1-30/5 10 ? * MON-SAT", date);
        Assert.assertEquals(Collections.emptyList(), calculate);


    }
}
