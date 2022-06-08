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



  private ComponentState currentState;

  @Override
  public void setup(IScheduler s, ISimulationErrorHandler eh, BackTrackHandler backTrackHandler) {
    super.setup(s, eh, backTrackHandler);
    currentState = new ComponentState(AutomataState.ONE, null, null, null, null, getComponentName(), null);

  }

  @Override
  protected void timeStep() {

  }

  @Override
  public void treattIn(Integer msg) {
    ComponentState output = saveState(this, findPossIn(msg));
    if(output != null) {
      currentState = output;
      currentState.setMessageNotSend(false);
      Map<IPort, List<TickedMessage>> outmsgs = currentState.getOutMessages();
      for (IPort outport : outmsgs.keySet())
        for (TickedMessage outmsg : outmsgs.get(outport)) {
          outport.send(outmsg);
        }
    }
  }

  public List<ComponentState> findPossIn(int m) {
    List<ComponentState> posscompstates = new LinkedList<>();

    if (currentState.getCurrentState() == AutomataState.ONE) {
      if (m >= 0) {
        Map<IPort, List<TickedMessage>> outmsg = new HashMap<>();
        outmsg.put((IPort) gettOut(), List.of(Message.of(-m)));
        posscompstates.add(new ComponentState(AutomataState.ONE, null, (IPort) gettIn(), Message.of(m), outmsg, getComponentName(), "z>=0"));
      }
      if (m < 0) {
        Map<IPort, List<TickedMessage>> outmsg = new HashMap<>();
        outmsg.put((IPort) gettOut(), List.of(Message.of(m)));
        posscompstates.add(new ComponentState(AutomataState.ONE, null, (IPort) gettIn(), Message.of(m), outmsg, getComponentName(), "z<0"));
      }
    }




    return posscompstates;
  }

  @Override
  public ComponentState getComponentState() {
    return currentState;
  }

  @Override
  public void setComponentState(ComponentState cs){
    this.currentState=cs;
    if(currentState.getMessageNotSend()){
      currentState.setMessageNotSend(false);
      for(IPort outport: cs.getOutMessages().keySet()){
        for(TickedMessage outmsg : cs.getOutMessages().get(outport)){
          gettOut().send(outmsg);
        }
      }
    }
  }

}
