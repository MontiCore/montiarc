/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.runtimes;

import org.slf4j.LoggerFactory;

/**
 * Default methods for logging.
 * 
 */
public class Log {

  public static void warn(String tag, String msg) {
    LoggerFactory.getLogger(tag).warn(msg);
  }
  
  public static void error(String tag, String msg) {
    LoggerFactory.getLogger(tag).error(msg);
  }
  
  public static void error(String tag, Exception e) {
    LoggerFactory.getLogger(tag).error(e.getMessage(),e);
  }

  public static void error(String tag, String msg, Exception e){
    LoggerFactory.getLogger(tag).error(msg,e);
  }
  
  public static void log(String tag, String msg) {
    LoggerFactory.getLogger(tag).info(msg);
  }
}
