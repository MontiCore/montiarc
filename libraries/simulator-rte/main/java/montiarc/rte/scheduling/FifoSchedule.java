/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

/**
 * A basic scheduler in which execution order is based on order of registration in a FIFO Queue.
 */
public class FifoSchedule extends AbstractSchedule {
  
  Queue<Computation> computations = new ArrayDeque<>();
  
  @Override
  protected void addComputationToCollection(Computation computation) {
    computations.add(computation);
  }
  
  @Override
  protected Optional<Computation> getNextComputation() {
    return Optional.ofNullable(computations.poll());
  }
}
