package top.jfunc.cron;

import org.junit.Test;
import top.jfunc.cron.util.CronSequenceGenerator;
import top.jfunc.cron.util.DateUtil;

import java.util.Date;

/**
 * @author xiongshiyan at 2018/11/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class CronNextTest {
    @Test
    public void testNext(){
        Date date = DateUtil.toDate("2018-11-18 12:00:12");
        String cron = "2 15 12 ? * *";
        Date next = new CronSequenceGenerator(cron).next(date);
        System.out.println(DateUtil.toStr(next));
    }
}
