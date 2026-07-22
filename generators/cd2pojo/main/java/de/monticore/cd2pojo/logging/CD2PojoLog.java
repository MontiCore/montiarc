/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo.logging;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.RichConsoleLogHook;

import java.util.ArrayList;

public class CD2PojoLog extends Log {

  /**
   * Initialize the Log directly as CD2PojoLog (incl. INFO)
   */
  public static void init() {
    CD2PojoLog log = new CD2PojoLog();
    log.isTRACE = false;
    log.isDEBUG = false;
    log.isINFO = false;
    log.logHooks = new ArrayList<>();
    log.logHooks.add(new RichConsoleLogHook());
    log.errorHook = getDefaultErrorHook();
    Log.setLog(log);
  }

  /**
   * Initialize the Log directly as CD2PojoLog (incl. DEBUG and INFO)
   */
  public static void initDEBUG() {
    CD2PojoLog log = new CD2PojoLog();
    log.isTRACE = false;
    log.isDEBUG = true;
    log.isINFO = true;
    log.logHooks = new ArrayList<>();
    log.logHooks.add(new RichConsoleLogHook());
    log.errorHook = getDefaultErrorHook();
    Log.setLog(log);
  }

  /**
   * Initialize the Log directly as CD2PojoLog (incl. TRACE, DEBUG, INFO)
   */
  public static void initTRACE() {
    CD2PojoLog log = new CD2PojoLog();
    log.isTRACE = true;
    log.isDEBUG = true;
    log.isINFO = true;
    log.logHooks = new ArrayList<>();
    log.logHooks.add(new RichConsoleLogHook());
    log.errorHook = getDefaultErrorHook();
    Log.setLog(log);
  }
}
