/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import sim.IScheduler;
import sim.generic.ISimComponent;
import sim.generic.Message;
import sim.help.PrioMsgComparator;
import sim.port.IInSimPort;
import sim.port.IPortFactory;

import java.util.*;

/**
 * @deprecated use {@link SchedulerFactory#createPrioScheduler()} instead.
 */
@Deprecated
public class PrioMsgSchedulerDeprecated extends DefaultSimSchedulerDeprecated implements IScheduler {

  protected Map<IInSimPort<?>, List<Message<?>>> bufferedMsgs;

  protected Comparator<Message<?>> comparator = new PrioMsgComparator();

  /**
   * @deprecated use {@link PrioMsgScheduler} instead. PrioMsgSchedulerDeprecated is for tests only.
   */
  public PrioMsgSchedulerDeprecated() {
    super();
    bufferedMsgs = new HashMap<IInSimPort<?>, List<Message<?>>>();
  }

  @Override
  protected boolean processData(ISimComponent comp, IInSimPort<?> port, Message<?> msg) {
    bufferedMsgs.get(port).add(msg);
    return true;
  }

  @Override
  protected boolean processTick(ISimComponent comp, IInSimPort<?> port) {
    Set<IInSimPort<?>> tickless = comp2tickless.get(comp);
    boolean success = tickless.remove(port);

    if (success && tickless.isEmpty()) {

      processBufferedMsgs(comp);

      comp.handleTick();
      // wake ports up
      for (IInSimPort<?> p : comp2Ports.get(comp)) {
        if (!p.hasTickReceived()) {
          tickless.add(p);
        }
        p.wakeUp();
      }
      for (IInSimPort<?> p : comp2Ports.get(comp)) {
        p.processBufferedMsgs();
      }

    } else {
      success = false;
    }

    return success;
  }

  /**
   * Processes buffered messages for the given component.
   *
   * @param comp component with buffered messages.
   */
  protected void processBufferedMsgs(ISimComponent comp) {
    for (IInSimPort<?> port : comp2Ports.get(comp)) {
      List<Message<?>> msgs = new LinkedList<Message<?>>();
      msgs.addAll(bufferedMsgs.get(port));
      Collections.sort(msgs, comparator);
      bufferedMsgs.get(port).clear();
      for (Message<?> msg : msgs) {
        comp.handleMessage(port, msg);
      }
    }
  }

  /**
   * @see sim.IScheduler#setupPort(IInSimPort)
   */
  @Override
  public void setupPort(IInSimPort<?> port) {
    super.setupPort(port);
    if (!this.bufferedMsgs.containsKey(port)) {
      this.bufferedMsgs.put(port, new LinkedList<Message<?>>());
    }
  }
}
