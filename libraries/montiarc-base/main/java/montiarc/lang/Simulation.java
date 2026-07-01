/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

public class Simulation extends SimulationTOP {

  /**
   * Asks the scheduler to stop the simulation.
   * The currently scheduled components will complete their processing first.
   */
  public static void stop() {
    if (montiarc.rte.Simulation.coordinatingScheduler == null) throw new RuntimeException("No simulation is running, cannot stop simulation.");
    montiarc.rte.Simulation.coordinatingScheduler.stop();
  }

  public static Duration getTickLength() {
    return Duration.ofMilliseconds(montiarc.rte.Simulation.nanosecondsPerTick / 1000000);
  }
}
