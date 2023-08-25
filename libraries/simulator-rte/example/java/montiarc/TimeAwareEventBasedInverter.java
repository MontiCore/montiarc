/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.AbstractInPort;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public class TimeAwareEventBasedInverter implements ITimedComponent {

  protected final String name;

  public TimeAwareEventBasedInverter(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<ITimeAwareInPort<?>> getAllInPorts() {
    return List.of(bIn, iIn);
  }

  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of(bOut, iOut);
  }

  protected TimeAwareInPort<Boolean> bIn = new TimeAwareInPort<>(getName() + ".bIn", this);

  protected TimeAwareInPort<Integer> iIn = new TimeAwareInPort<>(getName() + ".iIn", this);

  protected TimeAwareOutPort<Boolean> bOut = new TimeAwareOutPort<>(getName() + ".bOut", this);
  protected TimeAwareOutPort<Integer> iOut = new TimeAwareOutPort<>(getName() + ".iOut", this);
  
  @Override
  public void handleMessage(AbstractInPort<?> receivingPort) {
    if(bIn.getQualifiedName().equals(receivingPort.getQualifiedName())) handleMessageOnBIn();
    else if(iIn.getQualifiedName().equals(receivingPort.getQualifiedName())) handleMessageOnIIn();
  }

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
    if (!areAllInputsTickBlocked()) return false;

    dropTickOnAllInputs();
    sendTickOnAllOutputs();

    //Follows: the 'handleMessageOn' method for each port.
    handleMessageOnBIn();
    handleMessageOnIIn();

    return true;

  }


  protected void handleMessageOnBIn() {
    if (this.bIn.isBufferEmpty()) { // sanity check, also required to set up shadowed variables correctly. If port is set up / used correctly, the buffer should never be empty at this point, making this check theoretically obsolete
      return;
    }

    if (tryHandleTick()) {
      return; // if the above method returned true, ticks on incoming ports were dropped and this method has already been called again, so we can exit here
    }

    Boolean bIn = this.bIn.pollBuffer().getData(); // shadow port variable in order to use code directly from the model

    if (bIn != null) { // code/condition taken directly from respective guard statement

      Boolean bOut = null; // shadow port variable in order to use code directly from the model (we want to shadow only the ports on which data is actually written)

      bOut = !bIn; // code taken directly from respective transition body

      this.bOut.send(bOut); // send shadowed variables as messages - do not send messages on ports that were not affected in the respective transition body

      return; // technically not required here, but present for explanation's sake. If multiple transitions are available and their conditions hold, we want to execute only one transition
    }
  }


  protected void handleMessageOnIIn() {
    if (this.iIn.isBufferEmpty()) { // sanity check, also required to set up shadowed variables correctly. If port is set up / used correctly, the buffer should never be empty at this point, making this check theoretically obsolete
      return;
    }

    if (tryHandleTick()) {
      return; // if the above method returned true, ticks on incoming ports were dropped and this method has already been called again, so we can exit here
    }

    Integer iIn = this.iIn.pollBuffer().getData(); // shadow port variable in order to use code directly from the model

    if (iIn != null) { // code/condition taken directly from respective guard statement

      Integer iOut = null; // shadow port variable in order to use code directly from the model (we want to shadow only the ports on which data is actually written)

      iOut = -1 * iIn; // code taken directly from respective transition body

      this.iOut.send(iOut); // send shadowed variables as messages - do not send messages on ports that were not affected in the respective transition body

      return; // technically not required here, but present for explanation's sake. If multiple transitions are available and their conditions hold, we want to execute only one transition
    }
  }
}
