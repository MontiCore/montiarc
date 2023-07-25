/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public class InverterComp implements InverterContext, ITimedComponent {

  protected InverterAut automaton;

  protected final String name;

  protected InverterComp(String name) {
    this.name = name;
    this.automaton = new InverterAutBuilder(this)
        .addDefaultStates()
        .addDefaultTransitions()
        .setDefaultInitial()
        .build();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public InverterAut getBehavior() {
    return automaton;
  }

  @Override
  public TimeAwareInPort<Boolean> port_i() {
    return this.port_i;
  }
  
  @Override
  public List<TimeAwareInPort<?>> getAllInPorts() {
    return InverterContext.super.getAllInPorts();
  }
  
  @Override
  public TimeAwareOutPort<Boolean> port_o() {
    return this.port_o;
  }
  
  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return InverterContext.super.getAllOutPorts();
  }

  protected TimeAwareInPort<Boolean> port_i = new TimeAwareInPort<>(getName() + ".i") {
    @Override
    protected void handleBuffer() {
      if (buffer.isEmpty()) return;

      handleMessageOnBIn();
    }
  };

  protected TimeAwareOutPort<Boolean> port_o = new TimeAwareOutPort<>(getName() + ".o");

  protected boolean areAllInputsTickBlocked() { // this method could be generated for all ports with time-aware input ports
    return this.port_i().isTickBlocked();
  }

  protected void dropTickOnAllInputs() { // this method could be generated for all ports with time-aware input ports
    this.port_i().dropBlockingTick();
  }

  protected void sendTickOnAllOutputs() { // this method could be generated for all ports with time-aware output ports
    this.port_o().sendTick();
  }

  protected void handleMessageOnBIn() {
    handleComputationOnSyncPorts();
  }

  protected void handleComputationOnSyncPorts() {
    boolean allPortsReady = !port_i.isBufferEmpty();

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