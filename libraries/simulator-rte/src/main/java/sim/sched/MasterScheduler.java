/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import sim.IScheduler;
import sim.help.DescendingIntComparator;
import sim.port.IForwardPort;
import sim.port.IInPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;
import sim.port.IPortFactory;
import sim.port.MasterSlavePortFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A Master Scheduler that controls several slave scheduler. Each slave can be associated with a priority. High
 * priorities are preferred, if buffered messages of silent ports are to be processed (see activate method).
 */
public final class MasterScheduler implements IPortFactory {

  private final Set<IInPort<?>> active;

  private final Map<IScheduler, Queue<IInSimPort<?>>> sched2silentPorts;

  private final SortedMap<Integer, Set<IScheduler>> prio2scheds;

  private static MasterScheduler theInstance = new MasterScheduler();

  public static final int DEFAULT_PRIORITY = 500;

  public static final int MIN_PRIORITY = 0;

  public static final int MAX_PRIORITY = 1000;

  /**
   * Used to produce ports.
   */
  protected IPortFactory portFactory;

  private MasterScheduler() {
    active = new HashSet<IInPort<?>>();
    sched2silentPorts = new HashMap<IScheduler, Queue<IInSimPort<?>>>();

    Comparator<Integer> reverseComparator = new DescendingIntComparator();
    prio2scheds = new TreeMap<Integer, Set<IScheduler>>(reverseComparator);
    portFactory = new MasterSlavePortFactory();
  }

  /**
   * @return a scheduler with default priority
   */
  public static IScheduler createScheduler() {
    return theInstance.doCreateScheduler(DEFAULT_PRIORITY);
  }

  /**
   * Creates a scheduler with the given priority.
   *
   * @param prio priority
   * @return scheduler with the given priority
   */
  public static IScheduler createScheduler(int prio) {
    return theInstance.doCreateScheduler(prio);
  }

  /**
   * Creates a scheduler with the given priority.
   *
   * @param priority priority
   * @return scheduler with the given priority
   */
  public IScheduler doCreateScheduler(int priority) {
    int prio = priority;
    if (prio < MIN_PRIORITY) {
      prio = MIN_PRIORITY;
    } else if (prio > MAX_PRIORITY) {
      prio = MAX_PRIORITY;
    }

    if (!prio2scheds.containsKey(prio)) {
      prio2scheds.put(prio, new HashSet<IScheduler>());
    }
    IScheduler result = new SlaveScheduler(this);
    sched2silentPorts.put(result, new LinkedList<IInSimPort<?>>());

    prio2scheds.get(prio).add(result);

    return result;
  }

  public void setActive(IInPort<?> port) {
    active.add(port);
  }

  public void setInactive(IInPort<?> port) {
    active.remove(port);
  }

  public boolean isActive(IInPort<?> port) {
    return active.contains(port);
  }


  public void register(IScheduler sched, IInSimPort<?> silentPort) {
    Queue<IInSimPort<?>> silentPorts = sched2silentPorts.get(sched);
    if (!silentPorts.contains(silentPort)) {
      silentPorts.offer(silentPort);
    }
  }

  public void activate() {
    for (Integer currentPrio : prio2scheds.keySet()) {
      for (IScheduler sched : prio2scheds.get(currentPrio)) {
        for (IInSimPort<?> p : sched2silentPorts.get(sched)) {
          p.processBufferedMsgs();
        }
      }
    }
  }

  /**
   * @see sim.port.IPortFactory#createInPort()
   */
  @Override
  public <T> IInSimPort<T> createInPort() {
    return portFactory.createInPort();
  }

  /**
   * @see sim.port.IPortFactory#createOutPort()
   */
  @Override
  public <T> IOutSimPort<T> createOutPort() {
    return portFactory.createOutPort();
  }

  /**
   * @see sim.port.IPortFactory#createForwardPort()
   */
  @Override
  public <T> IForwardPort<T> createForwardPort() {
    return portFactory.createForwardPort();
  }

  /**
   * @param fact factory tu use
   */
  public void setPortFactory(IPortFactory fact) {
    this.portFactory = fact;
  }
}
