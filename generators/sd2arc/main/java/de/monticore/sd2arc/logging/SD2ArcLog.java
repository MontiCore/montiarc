/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.logging;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.RichConsoleLogHook;

import java.util.ArrayList;

public class SD2ArcLog extends Log {
  public static void init() {
    SD2ArcLog l = new SD2ArcLog();
    l.isTRACE = false;
    l.isDEBUG = false;
    l.isINFO = true;
    l.logHooks = new ArrayList<>();
    l.logHooks.add(new RichConsoleLogHook());
    l.errorHook = getDefaultErrorHook();
    Log.setLog(l);
  }
}
