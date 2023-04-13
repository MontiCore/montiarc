/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduler;

/**
 * Scheduler implementation acting like there is no scheduler.
 * <br>
 * This class implements behavior contradicting the idea of a scheduler:
 * Any incoming computation is performed instantly, with no regard for any other running computations.
 * This is most similar to a simulation without schedulers.
 */
public class InstantScheduler implements IScheduler {

  /**
   * Register the given computation to be executed according to this scheduler's system.
   * <br>
   * In this scheduler, that means executing the computation instantly.
   *
   * @param computation the computation to be scheduled by this
   */
  @Override
  public void registerComputation(Computation computation) {
    computation.run();
  }
}
