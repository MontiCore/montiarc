/* (c) https://github.com/MontiCore/monticore */
package montiarc.decomposed;

import montiarc.rte.components.ITimedComponent;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public class Counter implements ITimedComponent {
  
  protected final String qualifiedInstanceName;
  
  public Counter(String qualifiedInstanceName) {
    this.qualifiedInstanceName = qualifiedInstanceName;
  }
  
  @Override
  public String getQualifiedInstanceName() {
    return qualifiedInstanceName;
  }
  
  @Override
  public List<TimeAwareInPort<?>> getAllInPorts() {
    return List.of(iIn);
  }
  
  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of(count);
  }
  
  Integer cnt = 0;
  
  TimeAwareInPort<Integer> iIn = new TimeAwareInPort<>(getQualifiedInstanceName() + ".iIn") {
    @Override
    protected void handleBuffer() {
      if (buffer.isEmpty()) return;
      
      handleMessageOnIIn();
    }
  };
  
  TimeAwareOutPort<Integer> count = new TimeAwareOutPort<>(getQualifiedInstanceName() + ".count");
  
  protected boolean areAllInputsTickBlocked() { // this method could be generated for all ports with time-aware input ports
    return this.iIn.isTickBlocked();
  }
  
  protected void dropTickOnAllInputs() { // this method could be generated for all ports with time-aware input ports
    this.iIn.dropBlockingTick();
  }
  
  protected void sendTickOnAllOutputs() { // this method could be generated for all ports with time-aware output ports
    this.count.sendTick();
  }
  
  protected boolean tryHandleTick() { // this method could be generated for all ports with time-aware input ports
    if(!areAllInputsTickBlocked()) return false;
    
    dropTickOnAllInputs();
    sendTickOnAllOutputs();
    
    //Follows: the 'handleMessageOn' method for each port.
    handleMessageOnIIn();
    
    return true;
    
  }
  
  protected void handleMessageOnIIn() {
    if(this.iIn.isBufferEmpty()) return;
    
    if(tryHandleTick()) {
      return; // if the above method returned true, ticks on incoming ports were dropped and this method has already been called again, so we can exit here
    }
    
    Integer iIn = this.iIn.pollBuffer().getValue();
    Integer count = null;
    
    cnt = cnt + 1;
    count = cnt;
    
    this.count.sendMessage(count);
    
    return;
  }
}
