/* (c) https://github.com/MontiCore/monticore */
package montiarc.decomposed;

import montiarc.rte.components.ITimedComponent;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public class Sorter implements ITimedComponent {
  
  protected final String qualifiedInstanceName;
  
  public Sorter(String qualifiedInstanceName) {
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
    return List.of(gtEq0, lt0);
  }
  
  TimeAwareInPort<Integer> iIn = new TimeAwareInPort<>(getQualifiedInstanceName() + ".iIn") {
    @Override
    protected void handleBuffer() {
      if (buffer.isEmpty()) return;
  
      handleMessageOnIIn();
    }
  };
  
  TimeAwareOutPort<Integer> gtEq0 = new TimeAwareOutPort<>(getQualifiedInstanceName() + ".gtEq0");
  TimeAwareOutPort<Integer> lt0 = new TimeAwareOutPort<>(getQualifiedInstanceName() + ".lt0");
  
  protected boolean areAllInputsTickBlocked() { // this method could be generated for all ports with time-aware input ports
    return this.iIn.isTickBlocked();
  }
  
  protected void dropTickOnAllInputs() { // this method could be generated for all ports with time-aware input ports
    this.iIn.dropBlockingTick();
  }
  
  protected void sendTickOnAllOutputs() { // this method could be generated for all ports with time-aware output ports
    this.gtEq0.sendTick();
    this.lt0.sendTick();
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
    
    if(iIn >= 0) {
      Integer gtEq0 = null;
      
      gtEq0 = iIn;
      
      this.gtEq0.sendMessage(gtEq0);
      
      return;
    }
    
    if(iIn < 0) {
      Integer lt0 = null;
      
      lt0 = iIn;
      
      this.lt0.sendMessage(lt0);
      
      return;
    }
  }
}