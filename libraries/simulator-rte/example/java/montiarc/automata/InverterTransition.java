/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.automaton.transition.Action;
import montiarc.rte.automaton.transition.Guard;
import montiarc.rte.automaton.transition.Transition;
import montiarc.rte.port.AbstractInPort;

import java.util.List;

class InverterTransition implements Transition {
  protected InverterState sourceState;
  protected InverterState targetState;

  protected Guard guard;
  protected Action transitionAction;

  protected List<AbstractInPort<?>> usedInPorts;

  public InverterTransition(InverterState sourceState,
                            InverterState targetState,
                            Guard guard,
                            Action transitionAction,
                            List<AbstractInPort<?>> usedInPorts) {
    this.sourceState = sourceState;
    this.targetState = targetState;
    this.guard = guard;
    this.transitionAction = transitionAction;
    this.usedInPorts = usedInPorts;
  }

  @Override
  public InverterState getSourceState() {
    return sourceState;
  }

  @Override
  public InverterState getTargetState() {
    return targetState;
  }

  public Guard getGuard() {
    return guard;
  }

  public InverterState performTransition(Inverter context) { // returns the state the automaton is in after transitioning (relevant for hierarchical state spaces)
    getSourceState().exitAction(context);
    transitionAction.perform();
    InverterState actualTarget = targetState.entryAction(context);

    usedInPorts.forEach(AbstractInPort::pollBuffer);

    return actualTarget;
  }
}
