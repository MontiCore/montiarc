/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import sim.automaton.AutomataState;
import sim.automaton.ComponentState;
import sim.error.ISimulationErrorHandler;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.port.IPort;
import sim.sched.IScheduler;
import sim.serialiser.BackTrackHandler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FinalCompAut extends FinalComp implements Serializable {

  private ComponentState currentState;

  @Override
  public void setup(IScheduler s, ISimulationErrorHandler eh, BackTrackHandler backTrackHandler) {
    currentState = new ComponentState(AutomataState.ONE, null, null, null, null);
    super.setup(s, eh, backTrackHandler);
  }

  public List<ComponentState> findPossInInt(int m) {
    List<ComponentState> posscompstates = new LinkedList<>();

    if (currentState.getCurrentState() == AutomataState.ONE) {
      if (m >= 0) {
        Map<IPort, List<TickedMessage>> outmsg = new HashMap<>();
        outmsg.put((IPort) getmOut(), List.of(Message.of(1)));
        posscompstates.add(new ComponentState(AutomataState.ONE, null, (IPort) getmInInt(), Message.of(m), outmsg));
      }
      if (m < 0) {
        Map<IPort, List<TickedMessage>> outmsg = new HashMap<>();
        outmsg.put((IPort) getmOut(), List.of(Message.of(2)));
        posscompstates.add(new ComponentState(AutomataState.ONE, null, (IPort) getmInInt(), Message.of(m), outmsg));
      }
    }
    if (currentState.getCurrentState() == AutomataState.TWO) {
      if (m > 0) {
        Map<IPort, List<TickedMessage>> outmsg = new HashMap<>();
        outmsg.put((IPort) getmOut(), List.of(Message.of(3)));
        posscompstates.add(new ComponentState(AutomataState.ONE, null, (IPort) getmInInt(), Message.of(m), outmsg));
      }
      if (m <= 0) {
        Map<IPort, List<TickedMessage>> outmsg = new HashMap<>();
        outmsg.put((IPort) getmOut(), List.of(Message.of(4)));
        posscompstates.add(new ComponentState(AutomataState.ONE, null, (IPort) getmInInt(), Message.of(m), outmsg));
      }
    }

    if (posscompstates.isEmpty()) {
      ComponentState noTransitiontaken = new ComponentState(currentState.getCurrentState(), currentState.getStateVariables(), (IPort) getmInInt(), Message.of(m), null);
      posscompstates.add(noTransitiontaken);
    }

    return posscompstates;
  }

  public List<ComponentState> findPossInBool(boolean m) {
    List<ComponentState> posscompstates = new LinkedList<>();


    if (currentState.getCurrentState() == AutomataState.ONE) {
      if (m == false) {
        posscompstates.add(new ComponentState(AutomataState.TWO, null, (IPort) getmInBool(), Message.of(m), null));
      }
    }
    if (currentState.getCurrentState() == AutomataState.TWO) {
      if (m == true) {
        posscompstates.add(new ComponentState(AutomataState.ONE, null, (IPort) getmInBool(), Message.of(m), null));
      }
    }

    if (posscompstates.isEmpty()) {
      ComponentState noTransitiontaken = new ComponentState(currentState.getCurrentState(), currentState.getStateVariables(), (IPort) getmInBool(), Message.of(m), null);
      posscompstates.add(noTransitiontaken);
    }

    return posscompstates;
  }

  @Override
  protected void timeStep() {

  }

  @Override
  public void treatmInInt(Integer msg) {
    currentState = saveState(this, findPossInInt(msg));
    Map<IPort, List<TickedMessage>> outmsgs = currentState.getOutMessages();
    for (IPort outport : outmsgs.keySet())
      for (TickedMessage outmsg : outmsgs.get(outport)) {
        outport.send(outmsg);
      }
  }

  @Override
  public void treatmInBool(Boolean msg) {
    currentState = saveState(this, findPossInBool(msg));
    Map<IPort, List<TickedMessage>> outmsgs = currentState.getOutMessages();
    if (outmsgs != null) {
      for (IPort outport : outmsgs.keySet())
        for (TickedMessage outmsg : outmsgs.get(outport)) {
          outport.send(outmsg);
        }
    }
  }

}
