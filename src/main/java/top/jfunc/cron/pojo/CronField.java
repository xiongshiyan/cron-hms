package top.jfunc.cron.pojo;

import top.jfunc.cron.util.CronShapingUtil;

/**
 * cron表达式的域
 * @author xiongshiyan
 */
public class CronField {
    private CronPosition cronPosition;
    private String express;

    public CronField(CronPosition cronPosition, String express) {
        this.cronPosition = cronPosition;
        this.express = express;
    }

    public CronPosition getCronPosition() {
        return cronPosition;
    }

    public void setCronPosition(CronPosition cronPosition) {
        this.cronPosition = cronPosition;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    @Override
    public String toString() {
        return "CronField{" +
                "cronPosition=" + cronPosition +
                ", express='" + express + '\'' +
                '}';
    }
}
