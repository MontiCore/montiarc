/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.IComponent;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * A basic scheduler in which execution order is based on order of registration in a FIFO Queue.
 */
public class FifoSchedule extends AbstractSchedule {

  Queue<IComputation> computations = new ArrayDeque<>();

  @Override
  protected void addComputationToCollection(IComputation computation) {
    computations.add(computation);
  }

  @Override
  protected Optional<IComputation> getNextComputation() {
    Optional<IComputation> computation = Optional.empty();
    Iterator<IComputation> iterator = computations.iterator();
    while (iterator.hasNext() && computation.isEmpty()) {
      IComputation c = iterator.next();
      if (canSchedule(c)) {
        computation = Optional.of(c);
        iterator.remove();
      }
    }
    return computation;
  }

  @Override
  protected List<IComputation> getComputationsFor(IComponent component) {
    return computations.stream().filter(c -> c.getOwner() == component).collect(Collectors.toList());
  }
}
