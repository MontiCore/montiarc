/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.rte.oracle.OracleFactory;
import montiarc.rte.scheduling.Scheduler;

// HWC extension to make the behavior public (needed by the test)
public class SinkCompImpl extends SinkCompImplTOP {

  @Override
  public SinkEvents getBehavior() {
    return super.getBehavior();
  }

  protected SinkCompImpl(String name, Scheduler scheduler, OracleFactory oracleFactory) {
    super(name, scheduler, oracleFactory);
  }
}
