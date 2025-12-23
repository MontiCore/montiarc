/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.rte.scheduling.CoordinatingScheduler;

public class Simulation extends SimulationTOP {

  /**
   * The coordinating scheduler of the currently running simulation
   */
  public static CoordinatingScheduler coordinatingScheduler;

  /**
   * Should be used only for logs and other non-functional contexts.
   * It can overflow.
   */
  public static long ticks = 0;

  /**
   * The simulated time that passes with every tick in nanoseconds.
   */
  public static long nanosecondsPerTick = 0;

  /**
   * Asks the scheduler to stop the simulation.
   * The currently scheduled components will complete their processing first.
   */
  public static void stop() {
    if (coordinatingScheduler == null) throw new RuntimeException("No simulation is running, cannot stop simulation.");
    coordinatingScheduler.stop();
  }

  public static Duration getTickLength() {
    return Duration.ofMilliseconds(nanosecondsPerTick / 1000000);
  }
}
