/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.rte.scheduling.CoordinatingScheduler;

public class Simulation extends SimulationTOP {

  public static CoordinatingScheduler coordinatingScheduler;

  public static void stop() {
    if (coordinatingScheduler == null) throw new RuntimeException("No simulation is running, cannot stop simulation.");
    coordinatingScheduler.stop();
  }
}
