/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.IComponent;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * A basic scheduler in which execution order is based on order of registration in a LIFO Stack.
 */
public class LifoSchedule extends AbstractSchedule {

  Stack<IComputation> computations = new Stack<>();

  @Override
  protected void addComputationToCollection(IComputation computation) {
    computations.push(computation);
  }

  @Override
  protected Optional<IComputation> getNextComputation() {
    return computations.isEmpty() ? Optional.empty() : Optional.of(computations.pop());
  }

  @Override
  protected List<IComputation> getComputationsFor(IComponent component) {
    return computations.stream().filter(c -> c.getOwner() == component).collect(Collectors.toList());
  }
}
