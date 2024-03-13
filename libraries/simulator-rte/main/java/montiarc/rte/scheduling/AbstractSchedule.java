/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.IComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract base for a simple collection-based scheduler.
 * <br>
 * When a new computation is registered, it is first added to an internal collection.
 * Then, the next computation (according to the specific scheduler's system) is retrieved and executed.
 * As long as more computations are available, they will be executed in order.
 */
public abstract class AbstractSchedule implements ISchedule {

  protected boolean runningScheduler = false;
  protected boolean runningComputation = false;
  protected Map<IComponent, Long> timeSlot = new HashMap<>();
  protected long runUntilTick = -1;

  @Override
  public void registerComputation(IComputation computation) {
    if (computation instanceof TickComputation && getComputationsFor(computation.getOwner()).stream().anyMatch(TickComputation.class::isInstance)) {
      return; // Tick already scheduled for component
    }
    addComputationToCollection(computation);

    triggerNextComputation();
  }

  @Override
  public void register(IComponent component) {
    timeSlot.put(component, 0L);
  }

  /**
   * Run the next round of computations.
   * <br>
   * Can be used to externally trigger execution of
   * the next set of computations without registering another computation.
   */
  public void triggerNextComputation() {
    if (!runningScheduler || runningComputation) {
      return;
    }

    Optional<IComputation> nextComputation = getNextComputation();

    nextComputation.ifPresent(this::executeComputation);
  }

  protected void executeComputation(IComputation computation) {
    this.runningComputation = true;
    computation.run();
    this.runningComputation = false;

    // track ticks
    if (computation instanceof TickComputation) {
      timeSlot.put(computation.getOwner(), timeSlot.getOrDefault(computation.getOwner(), 0L) + 1);
      if (runUntilTick <= currentTick()) {
        runningScheduler = false;
        return;
      }
    }

    Optional<IComputation> nextComputation = getNextComputation();

    nextComputation.ifPresent(this::executeComputation);
  }

  protected abstract void addComputationToCollection(IComputation computation);

  protected abstract Optional<IComputation> getNextComputation();

  protected abstract List<IComputation> getComputationsFor(IComponent component);

  protected boolean canSchedule(IComputation computation) {
    return timeSlot.getOrDefault(computation.getOwner(), 0L) <= currentTick();
  }

  protected long currentTick() {
    return timeSlot.values().stream().reduce(Long.MAX_VALUE, Math::min);
  }

  @Override
  public void run() {
    runningScheduler = true;
    runUntilTick = -1;
    triggerNextComputation();
  }

  @Override
  public void run(int ticks) {
    if (ticks < 1) return;
    runningScheduler = true;
    runUntilTick = currentTick() + ticks;
    triggerNextComputation();
    runningScheduler = false;
  }
}
