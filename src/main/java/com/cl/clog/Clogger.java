package com.cl.clog;

import com.cl.constants.Constant;
import com.cl.logger.LogManager;
import com.cl.utils.CommUtil;
import com.cl.utils.TimeUtil;

/**
 * Created By chenli
 * ON 17/11/30
 */
public class Clogger {
    /**
     * 多线程环境下
     */
    private static volatile Clogger instance;
    private static LogManager logManager;

    static {
        logManager = LogManager.getInstance();
    }

    /**
     * JVM的钩子方法
     */
    public Clogger(){
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                close();
            }
        }));
    }

    /**
     * 饿汉模式
     * @return
     */
    public static Clogger getInstance(){
        if(instance == null){
            synchronized (Clogger.class) {
                if(instance == null){
                    instance = new Clogger();
                }
            }
        }
        return instance;
    }

    /**
     * 写调试日志
     * @param logMsg 日志内容
     */
    public void debug(String logMsg){
        writeLog("debug", Constant.DEBUG,logMsg);
    }

    /**
     * 写普通日志
     * @param logMsg 日志内容
     */
    public void info(String logMsg){
        writeLog("info",Constant.INFO,logMsg);
    }

    /**
     * 写警告日志
     * @param logMsg 日志内容
     */
    public void warn(String logMsg){
        writeLog("warn",Constant.WARN,logMsg);
    }

    /**
     * 写错误日志
     * @param logMsg 日志内容
     */
    public void error(String logMsg){
        writeLog("error",Constant.ERROR,logMsg);
    }

    /**
     * 写严重错误日志
     * @param logMsg 日志内容
     */
    public void fatal(String logMsg){
        writeLog("fatal",Constant.FATAL,logMsg);
    }

    /**
     * 写系统日志
     * @param level 日志级别
     * @param logMsg 日志内容
     */
    public void writeLog(int level,String logMsg){
        writeLog(Constant.LOG_DESC_MAP.get(String.valueOf(level)).toLowerCase(),level,logMsg);
    }

    /**
     * 写日志
     * @param logFileName 日志文件名
     * @param level 日志级别
     * @param logMsg 日志内容
     */
    public void writeLog(String logFileName, int level, String logMsg){
        if(logMsg != null && Constant.CFG_LOG_LEVEL.indexOf(""+level) >= 0){
            StringBuffer sb = new StringBuffer(logMsg.length() + 100);
            sb.append("[");
            sb.append(Constant.LOG_DESC_MAP.get(String.valueOf(level)));
            sb.append("] ");
            sb.append(TimeUtil.getFullDateTime());
            sb.append(" [");
            sb.append(Thread.currentThread().getName());
            sb.append("] ");
            sb.append(logMsg);
            sb.append("\n");
            logManager.addLog(logFileName, sb);

            //错误信息强制打印到控制台；若 CONSOLE_PRINT 配置为 true，也将日志打印到控制台
            if(Constant.ERROR == level || Constant.FATAL == level || Constant.CONSOLE_PRINT){
                try{
                    System.out.print(new String(sb.toString().getBytes(Constant.CFG_CHARSET_NAME),Constant.CFG_CHARSET_NAME));
                }catch(Exception e){
                    System.out.print(CommUtil.getExpStack(e));
                }
            }
        }
    }

    /**
     * 优雅关闭
     */
    private void close(){
        logManager.close();
    }
}
