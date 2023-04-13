/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduler;

import java.util.Optional;

/**
 * Abstract base for a simple collection-based scheduler.
 * <br>
 * When a new computation is registered, it is first added to an internal collection.
 * Then, the next computation (according to the specific scheduler's system) is retrieved and executed.
 * As long as more computations are available, they will be executed in order.
 */
public abstract class AbstractScheduler implements IScheduler {

  protected boolean runningComputation = false;

  @Override
  public void registerComputation(Computation computation) {
    addComputationToCollection(computation);

    triggerNextComputation();
  }

  /**
   * Run the next round of computations.
   * <br>
   * Can be used to externally trigger execution of
   * the next set of computations without registering another computation.
   */
  public void triggerNextComputation() {
    if (runningComputation) {
      return;
    }

    Optional<Computation> nextComputation = getNextComputation();

    nextComputation.ifPresent(this::executeComputation);
  }

  protected void executeComputation(Computation computation) {
    this.runningComputation = true;
    computation.run();
    this.runningComputation = false;

    Optional<Computation> nextComputation = getNextComputation();

    nextComputation.ifPresent(this::executeComputation);
  }

  protected abstract void addComputationToCollection(Computation computation);

  protected abstract Optional<Computation> getNextComputation();

}
