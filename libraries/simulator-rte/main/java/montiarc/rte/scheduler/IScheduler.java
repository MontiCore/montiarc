/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduler;

/**
 * This interface defines the main interaction point(s) with a scheduler used in a MontiArc simulation.
 * <br>
 * The scheduler decides the order in which components compute.
 * In order to have its computation scheduled, a component must register a {@link Computation} with the desired scheduler.
 * <br>
 * The registered computations should, in the end, be executed by the scheduler according to some system.
 */
public interface IScheduler {

  /**
   * Register the given computation to be executed according to this scheduler's system.
   *
   * @param computation the computation to be scheduled by this
   */
  void registerComputation(Computation computation);

}
