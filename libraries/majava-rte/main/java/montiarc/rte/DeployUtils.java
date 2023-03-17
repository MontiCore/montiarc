/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class DeployUtils {
  
  protected final static int DEFAULT_MAX_CYCLES_COUNT = 20;
  protected int maxCyclesCount;
  
  protected final static int DEFAULT_CYCLE_TIME = 50; // in ms
  protected int cycleTime;
  
  protected final static Path DEFAULT_LOG_PATH = Paths.get("./Log.txt");
  protected Path logPath;
  
  
  public boolean parseArgs(String[] args) {
    if (Arrays.stream(args).anyMatch(s -> s.matches("(-h)|(--h)|(-help)|(--help)")) || args.length > 3) {
      showHelp();
      return false;
    }
    maxCyclesCount = DEFAULT_MAX_CYCLES_COUNT;
    cycleTime = DEFAULT_CYCLE_TIME;
    logPath = DEFAULT_LOG_PATH;
    if(args.length >= 1) {
      maxCyclesCount = Integer.parseInt(args[0]);
    }
    if(args.length >= 2) {
      cycleTime = Integer.parseInt(args[1]);
    }
    if(args.length >= 3) {
      logPath = Paths.get(args[2]);
    }
    return true;
  }
  
  protected static void showHelp() {
    String firstLine = "Usage options:";
    String whitespace = repeat(" ", firstLine.length());
    System.out.println(firstLine + "\n" +
      whitespace + "<jar>\n" +
      whitespace + "<jar> <MAX_CYCLES_COUNT>\n" +
      whitespace + "<jar> <MAX_CYCLES_COUNT> <CYCLE_TIME>\n" +
      whitespace + "<jar> <MAX_CYCLES_COUNT> <CYCLE_TIME> <LOG_PATH>\n" +
      "\n" +
      "MAX_CYCLES_COUNT and CYCLE_TIME are positive integers, LOG_PATH is the path to a file or directory.\n" +
      "CYCLE_TIME is given in milliseconds.\n" +
      "\n" +
      "Defaults:\n" +
      whitespace + "MAX_CYCLES_COUNT = " + DEFAULT_MAX_CYCLES_COUNT + "\n" +
      whitespace + "CYCLE_TIME = " + DEFAULT_CYCLE_TIME + "\n" +
      whitespace + "LOG_PATH = " + DEFAULT_LOG_PATH);
  }
  
  private static String repeat(String s, int count) {
    return new String(new char[count]).replace("\0", s);
  }
  
  public int getCycleTime() {return this.cycleTime;}
  
  public int getMaxCyclesCount() {return this.maxCyclesCount;}
  
  public Path getLogPath() {return this.logPath;}
}