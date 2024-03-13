/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.IComponent;

/**
 * This interface defines the main interaction point(s) with a scheduler used in a MontiArc simulation.
 * <br>
 * The scheduler decides the order in which components compute.
 * In order to have its computation scheduled, a component must register a {@link Computation} with the desired scheduler.
 * <br>
 * The registered computations should, in the end, be executed by the scheduler according to some system.
 */
public interface ISchedule {

  /**
   * Register the given computation to be executed according to this scheduler's system.
   *
   * @param computation the computation to be scheduled by this
   */
  void registerComputation(IComputation computation);

  /**
   * Register the given component to be part of the scheduler.
   *
   * @param component the component
   */
  void register(IComponent component);


  /**
   * Run the simulation
   */
  void run();

  /**
   * Run the simulation for a specific number of ticks.
   * Calling this multiple times will continue the previous simulation.
   *
   * @param ticks the number of ticks the simulation should run
   */
  void run(int ticks);

}
