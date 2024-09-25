/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.tests.logging;

import de.se_rwth.commons.logging.DefaultErrorHook;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;

public class TestLogger extends Log {

  public static void init() {
    TestLogger l = new TestLogger();
    l.isTRACE = false;
    l.isDEBUG = false;
    l.isINFO = true;
    l.logHooks = new ArrayList<>();
    l.errorHook = new DefaultErrorHook();
    Log.setLog(l);
  }

  public static void initWARN() {
    TestLogger l = new TestLogger();
    l.isTRACE = false;
    l.isDEBUG = false;
    l.isINFO = false;
    l.logHooks = new ArrayList<>();
    l.errorHook = new DefaultErrorHook();
    Log.setLog(l);
  }

  public static void initTRACE() {
    TestLogger l = new TestLogger();
    l.isTRACE = true;
    l.isDEBUG = false;
    l.isINFO = true;
    l.logHooks = new ArrayList<>();
    l.errorHook = new DefaultErrorHook();
    Log.setLog(l);
  }

  public static void initDEBUG() {
    TestLogger l = new TestLogger();
    l.isDEBUG = true;
    l.isTRACE = true;
    l.isINFO = true;
    l.logHooks = new ArrayList<>();
    l.errorHook = new DefaultErrorHook();
    Log.setLog(l);
  }
}
