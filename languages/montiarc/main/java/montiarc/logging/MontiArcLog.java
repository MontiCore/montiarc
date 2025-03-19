/* (c) https://github.com/MontiCore/monticore */
package montiarc.logging;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.RichConsoleLogHook;

import java.util.ArrayList;

public class MontiArcLog extends Log {
  public static void init() {
    MontiArcLog l = new MontiArcLog();
    l.isTRACE = false;
    l.isDEBUG = false;
    l.isINFO = true;
    l.logHooks = new ArrayList<>();
    l.logHooks.add(new RichConsoleLogHook());
    l.errorHook = getDefaultErrorHook();
    Log.setLog(l);
  }
}
