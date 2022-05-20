/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import sim.comp.ISimComponent;
import sim.message.Message;
import sim.message.PrioMessage;
import sim.schedhelp.PrioMsgComparator;
import sim.port.IInSimPort;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Orders and then forwards {@link PrioMessage} messages to components.
 */
class PrioMsgScheduler extends SimSchedulerPortMap implements IScheduler {

  private final Map<IInSimPort<?>, List<Message<?>>> bufferedMsgs;

  private final Comparator<Message<?>> comparator = new PrioMsgComparator();

  protected PrioMsgScheduler() {
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
    IPortMap tickFree = getComp2tickfree().get(comp);

    boolean success = false;
    tickFree.setPortBlocked(port);

    if (tickFree.allPortsBlocked()) {
      success = true;
      processBufferedMsgs(comp);

      List<IInSimPort<?>> allPorts = getComp2SimPorts().get(comp);
      comp.handleTick();
      // wake ports up
      for (IInSimPort<?> p : allPorts) {
        p.wakeUp();
      }

      // reorganize tickfree ports
      for (IInSimPort<?> p : allPorts) {
        if (!p.hasTickReceived()) {
          tickFree.setPortTickfree(port);
        }
      }
      // trigger processing of buffered messages
      for (IInSimPort<?> p : allPorts) {
        p.processBufferedMsgs();
      }
    }
    return success;
  }

  /**
   * Processes buffered messages for the given component.
   *
   * @param comp component with buffered messages.
   */
  protected void processBufferedMsgs(ISimComponent comp) {
    for (IInSimPort<?> port : getComp2SimPorts().get(comp)) {
      List<Message<?>> msgs = new LinkedList<Message<?>>();
      msgs.addAll(bufferedMsgs.get(port));
      Collections.sort(msgs, comparator);
      bufferedMsgs.get(port).clear();
      for (Message<?> msg : msgs) {
        comp.handleMessage(port, msg);
        comp.checkConstraints();
      }
    }
  }

  /**
   * @see IScheduler#setupPort(IInSimPort)
   */
  @Override
  public void setupPort(IInSimPort<?> port) {
    super.setupPort(port);
    if (!this.bufferedMsgs.containsKey(port)) {
      this.bufferedMsgs.put(port, new LinkedList<Message<?>>());
    }
  }
}
