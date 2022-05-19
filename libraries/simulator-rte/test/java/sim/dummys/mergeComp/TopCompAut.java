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

public class TopCompAut extends TopComp implements Serializable {

  private AutomataState current;

  private ComponentState currentState;

  @Override
  public void setup(IScheduler s, ISimulationErrorHandler eh, BackTrackHandler backTrackHandler) {
    current = AutomataState.ONE;
    currentState = new ComponentState(current, null, null, null, null);

    super.setup(s, eh, backTrackHandler);
  }

  @Override
  protected void timeStep() {

  }

  @Override
  public void treattIn(Integer msg) {
    currentState = saveState(this, findPossIn(msg));
    Map<IPort, List<TickedMessage>> outmsgs = currentState.getOutMessages();
    for (IPort outport : outmsgs.keySet())
      for (TickedMessage outmsg : outmsgs.get(outport)) {
        outport.send(outmsg);
      }
  }

  public List<ComponentState> findPossIn(int m) {
    List<ComponentState> posscompstates = new LinkedList<>();

    if (current == AutomataState.ONE) {
      if (m >= 0) {
        Map<IPort, List<TickedMessage>> outmsg = new HashMap<>();
        outmsg.put((IPort) gettOut(), List.of(Message.of(-m)));
        posscompstates.add(new ComponentState(current, null, (IPort) gettIn(), Message.of(m), outmsg));
      }
      if (m < 0) {
        Map<IPort, List<TickedMessage>> outmsg = new HashMap<>();
        outmsg.put((IPort) gettOut(), List.of(Message.of(m)));
        posscompstates.add(new ComponentState(current, null, (IPort) gettIn(), Message.of(m), outmsg));
      }
    }


    return posscompstates;
  }

}
