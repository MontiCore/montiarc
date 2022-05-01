/* (c) https://github.com/MontiCore/monticore */
package sim.schedhelp;

import sim.message.Message;
import sim.message.PrioMessage;

import java.util.Comparator;

/**
 * Compares {@link PrioMessage}s according to their priority.
 */
public class PrioMsgComparator implements Comparator<Message<?>> {

  /**
   * Inner comparator.
   */
  private final DescendingIntComparator intComp = new DescendingIntComparator();

  /**
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(Message<?> msg1, Message<?> msg2) {
    int prio1, prio2;
    if (msg1 instanceof PrioMessage) {
      prio1 = ((PrioMessage<?>) msg1).getPriority();
    } else {
      prio1 = PrioMessage.DEFAULT_PRIORITY;
    }
    if (msg2 instanceof PrioMessage) {
      prio2 = ((PrioMessage<?>) msg2).getPriority();
    } else {
      prio2 = PrioMessage.DEFAULT_PRIORITY;
    }
    return intComp.compare(prio1, prio2);
  }
}
