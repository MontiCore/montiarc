/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public class Inverter implements ITimedComponent {

  protected InverterAutomaton automaton;

  protected final String qualifiedInstanceName;

  public Inverter(String qualifiedInstanceName) {
    this.qualifiedInstanceName = qualifiedInstanceName;
    this.automaton = new InverterAutomaton(this);
  }

  @Override
  public String getQualifiedInstanceName() {
    return qualifiedInstanceName;
  }

  @Override
  public List<TimeAwareInPort<?>> getAllInPorts() {
    return List.of(i);
  }

  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of(o);
  }

  protected TimeAwareInPort<Boolean> i = new TimeAwareInPort<>(getQualifiedInstanceName() + ".i") {
    @Override
    protected void handleBuffer() {
      if (buffer.isEmpty()) return;

      handleMessageOnBIn();
    }
  };

  protected TimeAwareOutPort<Boolean> o = new TimeAwareOutPort<>(getQualifiedInstanceName() + ".o");

  protected boolean areAllInputsTickBlocked() { // this method could be generated for all ports with time-aware input ports
    return this.i.isTickBlocked();
  }

  protected void dropTickOnAllInputs() { // this method could be generated for all ports with time-aware input ports
    this.i.dropBlockingTick();
  }

  protected void sendTickOnAllOutputs() { // this method could be generated for all ports with time-aware output ports
    this.o.sendTick();
  }

  protected void handleMessageOnBIn() {
    handleComputationOnSyncPorts();
  }

  protected void handleComputationOnSyncPorts() {
    boolean allPortsReady = !i.isBufferEmpty();

    if (!allPortsReady) return;

    if (areAllInputsTickBlocked()) {
      dropTickOnAllInputs();
      sendTickOnAllOutputs();
      handleComputationOnSyncPorts();
      return;
    }

    if (automaton.canExecuteTransition()) {
      automaton.executeAnyValidTransition();
    } else {
      // TODO discuss: if there is no valid transition at this point, should we drop the current inputs?
    }
  }
}