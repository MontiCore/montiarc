/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public class SyncInverter implements ITimedComponent {

  protected final String name;

  public SyncInverter(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<TimeAwareInPort<?>> getAllInPorts() {
    return List.of(bIn, iIn);
  }

  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of(bOut, iOut);
  }

  protected TimeAwareInPort<Boolean> bIn = new TimeAwareInPort<>(getName() + ".bIn") {
    @Override
    protected void handleBuffer() {
      if (buffer.isEmpty()) return;

      handleMessageOnBIn();
    }
  };

  protected TimeAwareInPort<Integer> iIn = new TimeAwareInPort<>(getName() + ".iIn") {
    @Override
    protected void handleBuffer() {
      if (buffer.isEmpty()) return;

      handleMessageOnIIn();
    }
  };

  protected TimeAwareOutPort<Boolean> bOut = new TimeAwareOutPort<>(getName() + ".bOut");
  protected TimeAwareOutPort<Integer> iOut = new TimeAwareOutPort<>(getName() + ".iOut");

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

  protected void handleMessageOnBIn() {
    handleComputationOnSyncPorts();
  }

  protected void handleMessageOnIIn() {
    handleComputationOnSyncPorts();
  }

  protected void handleComputationOnSyncPorts() {
    boolean allPortsReady = !bIn.isBufferEmpty() && !iIn.isBufferEmpty();

    if (!allPortsReady) return;

    if (areAllInputsTickBlocked()) {
      dropTickOnAllInputs();
      sendTickOnAllOutputs();
      handleComputationOnSyncPorts();
      return;
    }

    // shadow incoming port variables in order to use code directly from the model
    Boolean bIn = this.bIn.isTickBlocked() ? null : this.bIn.pollBuffer().getData(); // theoretically, the port should never be tick-blocked here
    Integer iIn = this.iIn.isTickBlocked() ? null : this.iIn.pollBuffer().getData(); // theoretically, the port should never be tick-blocked here

    // shadow outgoing port variables in order to use code directly from the model (we want to shadow only the ports on which data is actually written)
    Boolean bOut = null;
    Integer iOut = null;

    // code taken directly from respective transition body
    bOut = !bIn; // notice that this is not null-safe, but this is not an error with the RTE. Rather, the model would have to be adapted to fix this.
    iOut = -1 * iIn;

    // send shadowed variables as messages - do not send messages on ports that were not affected in the respective transition body
    this.bOut.send(bOut);
    this.iOut.send(iOut);
  }
}
