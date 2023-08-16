/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.AbstractOutPort;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public class EventInverterComp implements EventInverterContext, ITimedComponent {
  
  EventInverterAut automaton;
  
  protected final String name;
  
  protected EventInverterComp(String name) {
    this.name = name;
    this.automaton = new EventInverterAutBuilder(this)
        .addDefaultStates()
        .setDefaultInitial()
        .build();
  }
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public EventInverterAut getBehavior() {
    return automaton;
  }
  
  @Override
  public TimeAwareInPort<Boolean> port_bIn() {
    return this.port_bIn;
  }
  
  @Override
  public TimeAwareInPort<Integer> port_iIn() {
    return this.port_iIn;
  }
  
  @Override
  public List<ITimeAwareInPort<?>> getAllInPorts() {
    return EventInverterContext.super.getAllInPorts();
  }
  
  @Override
  public TimeAwareOutPort<Boolean> port_bOut() {
    return this.port_bOut;
  }
  
  @Override
  public TimeAwareOutPort<Integer> port_iOut() {
    return this.port_iOut;
  }
  
  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return EventInverterContext.super.getAllOutPorts();
  }
  
  protected TimeAwareInPort<Boolean> port_bIn = new TimeAwareInPort<>(getName() + ".i") {
    @Override
    protected void handleBuffer() {
      if (buffer.isEmpty()) return;
      
      if(this.isTickBlocked()) {
        handleTickEvent();
        return;
      }
      
      getBehavior().msg_bIn();
    }
  };
  
  protected TimeAwareInPort<Integer> port_iIn = new TimeAwareInPort<>(getName() + ".i") {
    @Override
    protected void handleBuffer() {
      if (buffer.isEmpty()) return;
  
      if(this.isTickBlocked()) {
        handleTickEvent();
        return;
      }
  
      getBehavior().msg_iIn();
    }
  };
  
  protected TimeAwareOutPort<Boolean> port_bOut = new TimeAwareOutPort<>(getName() + ".o");
  
  protected TimeAwareOutPort<Integer> port_iOut = new TimeAwareOutPort<>(getName() + ".o");
  
  protected boolean areAllInputsTickBlocked() {
    return getAllInPorts().stream().allMatch(ITimeAwareInPort::isTickBlocked);
  }
  
  protected void dropTickOnAllInputs() {
    getAllInPorts().forEach(ITimeAwareInPort::dropBlockingTick);
  }
  
  protected void sendTickOnAllOutputs() {
    getAllOutPorts().forEach(AbstractOutPort::sendTick);
  }
  
  protected void handleTickEvent() {
    if(areAllInputsTickBlocked()) {
      dropTickOnAllInputs();
      getBehavior().tick();
      sendTickOnAllOutputs();
      getAllInPorts().forEach(ITimeAwareInPort::continueAfterDroppedTick);
    }
  }
}
