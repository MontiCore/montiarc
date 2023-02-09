/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc.rte.components.ITimedComponent;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public class TimeAwareEventBasedInverter implements ITimedComponent {
  
  protected final String qualifiedInstanceName;
  
  public TimeAwareEventBasedInverter(String qualifiedInstanceName) {
    this.qualifiedInstanceName = qualifiedInstanceName;
  }
  
  @Override
  public String getQualifiedInstanceName() {
    return qualifiedInstanceName;
  }
  
  @Override
  public List<TimeAwareInPort<?>> getAllInPorts() {
    return List.of(bIn, iIn);
  }
  
  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of(bOut, iOut);
  }
  
  protected TimeAwareInPort<Boolean> bIn = new TimeAwareInPort<>(getQualifiedInstanceName() + ".bIn") {
    @Override
    protected void handleBuffer() {
      if(buffer.isEmpty()) return;
      
      handleMessageOnBIn();
    }
  };
  
  protected TimeAwareInPort<Integer> iIn = new TimeAwareInPort<>(getQualifiedInstanceName() + ".iIn") {
    @Override
    protected void handleBuffer() {
      if(buffer.isEmpty()) return;
      
      handleMessageOnIIn();
    }
  };
  
  protected TimeAwareOutPort<Boolean> bOut = new TimeAwareOutPort<>(getQualifiedInstanceName() + ".bOut");
  protected TimeAwareOutPort<Integer> iOut = new TimeAwareOutPort<>(getQualifiedInstanceName() + ".iOut");
  
  protected boolean areAllInputsTickBlocked() { // this method could be generated for all ports with time-aware input ports
    return this.bIn.isTickBlocked() && this.iIn.isTickBlocked();
  }
  
  protected void dropTickOnAllInputs() { // this method could be generated for all ports with time-aware input ports
    this.bIn.dropBlockingTick();
    this.iIn.dropBlockingTick();
  }
  
  protected void sendTickOnAllOutputs() { // this method could be generated for all ports with time-aware output ports
    this.bOut.sendTick();
    this.iOut.sendTick();
  }
  
  protected boolean tryHandleTick() { // this method could be generated for all ports with time-aware input ports
    if(!areAllInputsTickBlocked()) return false;
  
    dropTickOnAllInputs();
    sendTickOnAllOutputs();
  
    //Follows: the 'handleMessageOn' method for each port.
    handleMessageOnBIn();
    handleMessageOnIIn();
  
    return true;
    
  }
  
  
  protected void handleMessageOnBIn() {
    if(this.bIn.isBufferEmpty()) { // sanity check, also required to set up shadowed variables correctly. If port is set up / used correctly, the buffer should never be empty at this point, making this check theoretically obsolete
      return;
    }
    
    if(tryHandleTick()) {
      return; // if the above method returned true, ticks on incoming ports were dropped and this method has already been called again, so we can exit here
    }
    
    Boolean bIn = this.bIn.pollBuffer().getValue(); // shadow port variable in order to use code directly from the model
    
    if(bIn != null) { // code/condition taken directly from respective guard statement
  
      Boolean bOut = null; // shadow port variable in order to use code directly from the model (we want to shadow only the ports on which data is actually written)
      
      bOut = !bIn; // code taken directly from respective transition body
  
      this.bOut.sendMessage(bOut); // send shadowed variables as messages - do not send messages on ports that were not affected in the respective transition body
      
      return; // technically not required here, but present for explanation's sake. If multiple transitions are available and their conditions hold, we want to execute only one transition
    }
  }
  
  
  protected void handleMessageOnIIn() {
    if(this.iIn.isBufferEmpty()) { // sanity check, also required to set up shadowed variables correctly. If port is set up / used correctly, the buffer should never be empty at this point, making this check theoretically obsolete
      return;
    }
  
    if(tryHandleTick()) {
      return; // if the above method returned true, ticks on incoming ports were dropped and this method has already been called again, so we can exit here
    }
    
    Integer iIn = this.iIn.pollBuffer().getValue(); // shadow port variable in order to use code directly from the model
  
    if(iIn != null) { // code/condition taken directly from respective guard statement
  
      Integer iOut = null; // shadow port variable in order to use code directly from the model (we want to shadow only the ports on which data is actually written)
      
      iOut = -1 * iIn; // code taken directly from respective transition body
  
      this.iOut.sendMessage(iOut); // send shadowed variables as messages - do not send messages on ports that were not affected in the respective transition body
      
      return; // technically not required here, but present for explanation's sake. If multiple transitions are available and their conditions hold, we want to execute only one transition
    }
  }
  
  
}
