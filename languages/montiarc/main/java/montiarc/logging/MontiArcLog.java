/* (c) https://github.com/MontiCore/monticore */
package montiarc.logging;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.RichConsoleLogHook;

import java.util.ArrayList;

public class MontiArcLog extends Log {

  /**
   * Initialize the Log directly as MontiArcLog (incl. INFO)
   */
  public static void init() {
    MontiArcLog log = new MontiArcLog();
    log.isTRACE = false;
    log.isDEBUG = false;
    log.isINFO = true;
    log.logHooks = new ArrayList<>();
    log.logHooks.add(new RichConsoleLogHook());
    log.errorHook = getDefaultErrorHook();
    Log.setLog(log);
  }

  /**
   * Initialize the Log directly as MontiArcLog (incl. DEBUG and INFO)
   */
  public static void initDEBUG() {
    MontiArcLog log = new MontiArcLog();
    log.isTRACE = false;
    log.isDEBUG = true;
    log.isINFO = true;
    log.logHooks = new ArrayList<>();
    log.logHooks.add(new RichConsoleLogHook());
    log.errorHook = getDefaultErrorHook();
    Log.setLog(log);
  }

  /**
   * Initialize the Log directly as MontiArcLog (incl. TRACE, DEBUG, INFO)
   */
  public static void initTRACE() {
    MontiArcLog log = new MontiArcLog();
    log.isTRACE = true;
    log.isDEBUG = true;
    log.isINFO = true;
    log.logHooks = new ArrayList<>();
    log.logHooks.add(new RichConsoleLogHook());
    log.errorHook = getDefaultErrorHook();
    Log.setLog(log);
  }
}
