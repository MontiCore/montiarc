package de.montiarcautomaton.runtimes;

/**
 * Default methods for logging.
 * 
 * @author Gerrit Leonhardt
 */
public class Log {
  public static void warn(String tag, String msg) {
    log(tag, msg);
  }
  
  public static void error(String tag, String msg) {
    log(tag, msg);
  }
  
  public static void error(String tag, Exception e) {
    System.out.println("[" + tag + "]:");
    e.printStackTrace();
  }
  
  public static void log(String tag, String msg) {
    System.out.println("[" + tag + "]:\n" + msg);
  }
}
