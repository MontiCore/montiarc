/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte;

import montiarc.rte.scheduling.CoordinatingScheduler;

public class Simulation {

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
}
