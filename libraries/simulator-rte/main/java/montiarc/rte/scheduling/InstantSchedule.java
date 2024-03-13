/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.IComponent;

/**
 * Scheduler implementation acting like there is no scheduler.
 * <br>
 * This class implements behavior contradicting the idea of a scheduler:
 * Any incoming computation is performed instantly, with no regard for any other running computations.
 * This is most similar to a simulation without schedulers.
 */
public class InstantSchedule implements ISchedule {

  /**
   * Register the given computation to be executed according to this scheduler's system.
   * <br>
   * In this scheduler, that means executing the computation instantly.
   *
   * @param computation the computation to be scheduled by this
   */
  @Override
  public void registerComputation(IComputation computation) {
    computation.run();
  }

  @Override
  public void register(IComponent component) {
  }

  @Override
  public void run() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void run(int ticks) {
    throw new UnsupportedOperationException();
  }
}
