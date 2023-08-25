/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.*;

import java.util.List;
import java.util.NoSuchElementException;

public class VariabilityComp implements VariabilityContext, ITimedComponent {
  
  protected VariabilityAut automaton;
  
  protected final String name;
  
  protected final int param_a;
  protected final int param_b;
  
  protected final boolean feature_f1;
  protected final boolean feature_f2;
  protected final boolean feature_f3;
  
  
  protected VariabilityComp(String name) {
    this(name, 0, 0, false, false, false);
  }
  
  protected VariabilityComp(String name,
                            int a, int b,
                            boolean f1, boolean f2, boolean f3) {
    this.name = name;
    this.automaton = new VariabilityAutBuilder(this)
        .addDefaultStates()
        .addDefaultTransitions()
        .setDefaultInitial()
        .build();
    
    this.param_a = a;
    this.param_b = b;
    this.feature_f1 = f1;
    this.feature_f2 = f2;
    this.feature_f3 = f3;
  }
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public VariabilityAut getBehavior() {
    return automaton;
  }
  
  protected TimeAwareInPort<Integer> port_i1 = new TimeAwareInPort<>(getName() + ".i1", this);
  
  protected TimeAwareInPort<Integer> port_i2 = new TimeAwareInPort<>(getName() + ".i2", this);
  
  protected TimeAwareInPort<Integer> port_i3 = new TimeAwareInPort<>(getName() + ".i3", this);
  
  protected TimeAwareInPort<Integer> port_i4 = new TimeAwareInPort<>(getName() + ".i4", this);
  
  @Override
  public void handleMessage(AbstractInPort<?> receivingPort) {
    if (port_i1_available() && port_i1().getQualifiedName().equals(receivingPort.getQualifiedName())) handleMessageOnI1();
    else if (port_i2_available() && port_i2().getQualifiedName().equals(receivingPort.getQualifiedName())) handleMessageOnI2();
    else if (port_i3_available() && port_i3().getQualifiedName().equals(receivingPort.getQualifiedName())) handleMessageOnI3();
    else if (port_i4_available() && port_i4().getQualifiedName().equals(receivingPort.getQualifiedName())) handleMessageOnI4();
  }
  
  protected TimeAwareOutPort<Integer> port_o1 = new TimeAwareOutPort<>(getName() + ".o1", this);
  
  @Override
  public TimeAwareInPort<Integer> port_i1() {
    if(port_i1_available()) return this.port_i1; else throw new NoSuchElementException();
  }
  
  @Override
  public Boolean port_i1_available() {
    return (this.feature_f1);
  }
  
  @Override
  public TimeAwareInPort<Integer> port_i2() {
    if(port_i2_available()) return this.port_i2; else throw new NoSuchElementException();
  }
  
  @Override
  public Boolean port_i2_available() {
    return (!this.feature_f1) || (!this.feature_f2 || this.feature_f3 || 3 < 2);
  }
  
  @Override
  public TimeAwareInPort<Integer> port_i3() {
    if(port_i3_available()) return this.port_i3; else throw new NoSuchElementException();
  }
  
  @Override
  public Boolean port_i3_available() {
    return (!this.feature_f1) || (!this.feature_f2 || this.feature_f3 || 3 < 2);
  }
  
  @Override
  public TimeAwareInPort<Integer> port_i4() {
    if(port_i4_available()) return this.port_i4; else throw new NoSuchElementException();
  }
  
  @Override
  public Boolean port_i4_available() {
    return (!this.feature_f2 || this.feature_f3 || 3 < 2);
  }
  
  @Override
  public List<ITimeAwareInPort<?>> getAllInPorts() {
    return VariabilityContext.super.getAllInPorts();
  }
  
  @Override
  public TimeAwareOutPort<Integer> port_o1() {
    if(port_o1_available()) return this.port_o1; else throw new NoSuchElementException();
  }
  
  @Override
  public Boolean port_o1_available() {
    return (!this.feature_f2 || this.feature_f3 || 3 < 2);
  }
  
  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return VariabilityContext.super.getAllOutPorts();
  }
  
  protected boolean areAllInputsTickBlocked() {
    return this.getAllInPorts().stream().allMatch(ITimeAwareInPort::isTickBlocked);
  }
  
  protected void dropTickOnAllInputs() { // this method could be generated for all ports with time-aware input ports
    this.getAllInPorts().forEach(ITimeAwareInPort::dropBlockingTick);
  }
  
  protected void sendTickOnAllOutputs() { // this method could be generated for all ports with time-aware output ports
    this.getAllOutPorts().forEach(AbstractOutPort::sendTick);
  }
  
  protected void handleMessageOnI1() { handleComputationOnSyncPorts(); }
  
  protected void handleMessageOnI2() { handleComputationOnSyncPorts(); }
  
  protected void handleMessageOnI3() { handleComputationOnSyncPorts(); }
  
  protected void handleMessageOnI4() { handleComputationOnSyncPorts(); }
  
  protected void handleComputationOnSyncPorts() {
    boolean allPortsReady = getAllInPorts().stream()
        .filter(p -> p instanceof AbstractInPort<?>)
        .map(p -> (AbstractInPort<?>) p)
        .noneMatch(AbstractInPort::isBufferEmpty);
    
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
