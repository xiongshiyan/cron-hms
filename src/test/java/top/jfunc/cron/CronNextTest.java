package top.jfunc.cron;

import org.junit.Assert;
import org.junit.Test;
import top.jfunc.cron.util.CronSequenceGenerator;
import top.jfunc.cron.util.CronUtil;
import top.jfunc.cron.util.DateUtil;

import java.util.Date;

/**
 * @author xiongshiyan at 2018/11/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CronNextTest {
    @Test
    public void testNext1(){
        Date date = DateUtil.toDate("2018-11-18 12:00:12");
        String cron = "2 15 12 ? * *";
        Date next = new CronSequenceGenerator(cron).next(date);
        Assert.assertEquals("2018-11-18 12:15:02" , DateUtil.toStr(next));

        Date next1 = CronUtil.next(cron, date);
        Assert.assertEquals("2018-11-18 12:15:02" , DateUtil.toStr(next1));
    }
    @Test
    public void testNext2(){
        Date date = DateUtil.toDate("2011-03-25 13:22:43");
        String cron = "0 0 8 * * *";
        Date next = new CronSequenceGenerator(cron).next(date);
        Assert.assertEquals("2011-03-26 08:00:00" , DateUtil.toStr(next));

        Date next1 = CronUtil.next(cron, date);
        Assert.assertEquals("2011-03-26 08:00:00" , DateUtil.toStr(next1));
    }
    @Test
    public void testNext3(){
        Date date = DateUtil.toDate("2016-12-25 18:00:45");
        String cron = "0/2 1 * * * *";
        Date next = new CronSequenceGenerator(cron).next(date);
        Assert.assertEquals("2016-12-25 18:01:46" , DateUtil.toStr(next));

        Date next1 = CronUtil.next(cron, date);
        //18:01:46??? // 需要每个域大于当前的???
        Assert.assertEquals("2016-12-25 18:01:00" , DateUtil.toStr(next1));
    }
    @Test
    public void testNext4(){
        Date date = DateUtil.toDate("2016-01-29 04:01:12");
        String cron = "0 0/5 14,18 * * ?";
        Date next = new CronSequenceGenerator(cron).next(date);
        Assert.assertEquals("2016-01-29 14:00:00" , DateUtil.toStr(next));

        Date next1 = CronUtil.next(cron, date);
        Assert.assertEquals("2016-01-29 14:00:00" , DateUtil.toStr(next1));
    }
    @Test
    public void testNext5(){
        Date date = DateUtil.toDate("2022-08-31 23:59:59");
        String cron = "0 15 10 ? * MON-FRI";
        Date next = new CronSequenceGenerator(cron).next(date);
        Assert.assertEquals("2022-09-01 10:15:00" , DateUtil.toStr(next));

        Date next1 = CronUtil.next(cron, date);
        Assert.assertEquals("2022-09-01 10:15:00" , DateUtil.toStr(next1));
    }
    @Test
    public void testNext6(){
        Date date = DateUtil.toDate("2013-09-12 03:04:05");
        String cron = "0 26,29,33 * * * ?";
        Date next = new CronSequenceGenerator(cron).next(date);
        Assert.assertEquals("2013-09-12 03:26:00" , DateUtil.toStr(next));

        Date next1 = CronUtil.next(cron, date);
        Assert.assertEquals("2013-09-12 03:26:00" , DateUtil.toStr(next1));
    }
    @Test
    public void testNext7(){
        Date date = DateUtil.toDate("1999-10-18 12:00:00");
        String cron = "10-20/4 10,44,30/2 10 ? 3 WED";
        Date next = new CronSequenceGenerator(cron).next(date);
        Assert.assertEquals("2000-03-01 10:10:10" , DateUtil.toStr(next));

        Date next1 = CronUtil.next(cron, date);
        Assert.assertEquals("2000-03-01 10:10:10" , DateUtil.toStr(next1));
    }
    @Test
    public void testNext8(){
        Date date = DateUtil.toDate("2008-09-11 19:19:19");
        String cron = "0 0 0 1/2 MAR-AUG ?";
        Date next = new CronSequenceGenerator(cron).next(date);
        Assert.assertEquals("2009-03-01 00:00:00" , DateUtil.toStr(next));

        Date next1 = CronUtil.next(cron, date);
        Assert.assertEquals("2009-03-01 00:00:00" , DateUtil.toStr(next1));
    }
    @Test
    public void testNext9(){
        Date date = DateUtil.toDate("2003-02-09 06:17:19");
        String cron = "0 10-20/3,57-59 * * * WED-FRI";
        Date next = new CronSequenceGenerator(cron).next(date);
        Assert.assertEquals("2003-02-12 00:10:00" , DateUtil.toStr(next));

        Date next1 = CronUtil.next(cron, date);
        Assert.assertEquals("2003-02-12 00:10:00" , DateUtil.toStr(next1));
    }
    @Test
    public void testNext10(){
        Date date = DateUtil.toDate("2016-12-28 19:01:35");
        String cron = "0 10,44 14 ? 3 WED";
        Date next = new CronSequenceGenerator(cron).next(date);
        Assert.assertEquals("2017-03-01 14:10:00" , DateUtil.toStr(next));

        Date next1 = CronUtil.next(cron, date);
        Assert.assertEquals("2017-03-01 14:10:00" , DateUtil.toStr(next1));
    }

    @Test
    public void testNext11(){
        Date date = DateUtil.toDate("2018-11-18 12:00:12");
        String cron = "0-12/12 00 12 ? * *";
        Date next1 = CronUtil.next(cron, date);
        Assert.assertEquals("2018-11-18 12:00:12" , DateUtil.toStr(next1));
    }
}
