/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import java.util.Optional;
import java.util.Stack;

/**
 * A basic scheduler in which execution order is based on order of registration in a LIFO Stack.
 */
public class LifoSchedule extends AbstractSchedule {

  Stack<Computation> computations = new Stack<>();

  @Override
  protected void addComputationToCollection(Computation computation) {
    computations.push(computation);
  }

  @Override
  protected Optional<Computation> getNextComputation() {
    return computations.isEmpty() ? Optional.empty() : Optional.of(computations.pop());
  }
}
