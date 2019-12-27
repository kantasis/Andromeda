/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author kostis
 */
public class Logger {
    public static final int LL_CRITICAL=0;
    public static final int LL_ERROR=1;
    public static final int LL_WARNING=2;
    public static final int LL_INFO=3;
    public static final int LL_NOTICE=4;
    public static final int LL_DEBUG=5;
    public static final String[] LL_NAMES={
        "CRI",
        "ERR",
        "WRN",
        "NFO",
        "NTC",
        "DBG",
    };

    
    private static int _tabs_count=0;
    private static int _loglevel=0;
    private static int _loglevel_default=LL_DEBUG;
    
  
    /**
     * 
     * Poduces a log in stdout
     * @param ll loglevel
     * @param str The format string
     * @param x The list of objects
     */
    public static void log(int ll, String str, Object... x){
        if (getLogLevel()>ll)
            return;
        System.out.printf("<%s %s > %s",nowString(),LL_NAMES[ll],getTabs());
        System.out.printf(str+"\n",x);
    }
   
    /**
     * 
     * Poduces a log in stdout unsing the defaule loglevel
     * @param str The format string
     * @param x The list of objects
     */
    public static void log(String str, Object... x){
        log(getDefaultLogLevel(),str,x);
    }
   
    /**
     * 
     * Poduces a log in stdout unsing the defaule loglevel
     * @param str The format string
     */
    public static void log(String str){
        log(getDefaultLogLevel(),str,"");
    }
   
    /**
     * Method used to get the indentation of the logs
     * @return The indentation tabs of the logs
     */
    public static String getTabs(){
    String tabs="";
    for(int i=0;i<_tabs_count;i++)
        tabs+="\t";
    return tabs;
    }
   
    /**
     * Increases the indentation of subsequent logs
     */
    public static void indent(){
        _tabs_count++;
    }
   
    /**
     * Decreases the indentation of the logs
     */
    public static void dedent(){
       _tabs_count--;
    }
   
    /**
     * This method returns the current date and time
     * @return the formatted string of datetime
     */
    public static String nowString(){
       DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
       LocalDateTime now = LocalDateTime.now();  
       return dtf.format(now); 
    }
   
    /**
     * 
     * @return the loglevel of the Logger
     */
    public static int getLogLevel(){
       return _loglevel;
    }
   
    /** 
     * Sets the loglevel of the logger
     * @param x the new loglevel
     */
    public static void setLogLevel(int x){
       _loglevel=x;
    }

    /**
     * The loglevel if none is set
     * @return default loglevel
     */
    public static int getDefaultLogLevel(){
       return _loglevel_default;
    }
   
    /**
     * Sets the default loglevel
     * @param x the new default loglevel
     */
    public static void setDefaultLogLevel(int x){
       _loglevel_default=x;
    }
    
    
    public static int unitTest(){
        Logger.log("This is the first log %s",new Double(6));
        Logger.indent();
        Logger.log("This is an indented log\t%5f",6.66);
        Logger.log("This is the first log");
        return 0;
    }
    public static void main(String[] args){
        unitTest();
    }

}
