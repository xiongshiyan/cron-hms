package top.jfunc.cron.pojo;

/**
 * @author xiongshiyan at 2018/11/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class NotExecuteException extends Exception{
    public NotExecuteException(Exception e){
        super(e);
    }
    public NotExecuteException(String message){
        super(message);
    }
}
