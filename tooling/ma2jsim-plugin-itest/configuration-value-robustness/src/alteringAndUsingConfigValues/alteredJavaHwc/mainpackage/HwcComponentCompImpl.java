/* (c) https://github.com/MontiCore/monticore */
package mainpackage;

import montiarc.rte.oracle.OracleFactory;
import montiarc.rte.scheduling.Scheduler;

public class HwcComponentCompImpl extends HwcComponentCompImplTOP {
  protected HwcComponentSimComp(String name, Scheduler scheduler, OracleFactory oracleFactory) {
    super(name, scheduler, oracleFactory);
  }
}
