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

public class BottomCompAut extends BottomComp implements Serializable {

  private ComponentState currentState;

  public void setup(IScheduler s, ISimulationErrorHandler eh, BackTrackHandler backTrackHandler) {
    currentState = new ComponentState(AutomataState.ONE, null, null, null, null);

    super.setup(s, eh, backTrackHandler);
  }

  @Override
  protected void timeStep() {

  }

  @Override
  public void treatbIn(Integer msg) {
    currentState = saveState(this, findPossIn(msg));
    Map<IPort, List<TickedMessage>> outmsgs = currentState.getOutMessages();
    for (IPort outport : outmsgs.keySet())
      for (TickedMessage outmsg : outmsgs.get(outport)) {
        outport.send(outmsg);
      }
  }

  public List<ComponentState> findPossIn(int m) {
    List<ComponentState> posscompstates = new LinkedList<>();


    if (currentState.getCurrentState() == AutomataState.ONE) {
      if (m > 0) {
        Map<IPort, List<TickedMessage>> outmsg = new HashMap<>();
        outmsg.put((IPort) getbOut(), List.of(Message.of(true)));
        posscompstates.add(new ComponentState(AutomataState.ONE, null, (IPort) getbIn(), Message.of(m), outmsg));
      }
      if (m <= 0) {
        Map<IPort, List<TickedMessage>> outmsg = new HashMap<>();
        outmsg.put((IPort) getbOut(), List.of(Message.of(false)));
        posscompstates.add(new ComponentState(AutomataState.ONE, null, (IPort) getbIn(), Message.of(m), outmsg));
      }
    }

    if (posscompstates.isEmpty()) {
      ComponentState noTransitiontaken = new ComponentState(currentState.getCurrentState(), currentState.getStateVariables(), (IPort) getbIn(), Message.of(m), null);
      posscompstates.add(noTransitiontaken);
    }

    return posscompstates;
  }
}
