/* (c) https://github.com/MontiCore/monticore */
package barpackage;

import montiarc.rte.oracle.OracleFactory;
import montiarc.rte.scheduling.Scheduler;

public class BarCompImpl extends BarCompImplTOP {

  public BarCompImpl(String name, Scheduler scheduler, OracleFactory oracleFactory) {
    super(name, scheduler, oracleFactory);
  }
}
