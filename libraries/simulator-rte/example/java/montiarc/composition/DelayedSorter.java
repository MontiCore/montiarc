/* (c) https://github.com/MontiCore/monticore */
package montiarc.composition;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.*;

import java.util.List;

public class DelayedSorter implements ITimedComponent {

  protected final String name;

  public DelayedSorter(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<ITimeAwareInPort<?>> getAllInPorts() {
    return List.of(iIn);
  }

  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of(gtEq0, lt0);
  }

  TimeAwareInPort<Integer> iIn = new TimeAwareInPort<>(getName() + ".iIn", this);

  DelayedOutPort<Integer> gtEq0 = new DelayedOutPort<>(getName() + ".gtEq0", this, 1); // explicit delay of 1 is not required, only here for clarity
  DelayedOutPort<Integer> lt0 = new DelayedOutPort<>(getName() + ".lt0", this, 1); // explicit delay of 1 is not required, only here for clarity
  
  @Override
  public void handleMessage(AbstractInPort<?> receivingPort) {
    if(receivingPort.getQualifiedName().equals(iIn.getQualifiedName())) handleMessageOnIIn();
  }

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
    if (!areAllInputsTickBlocked()) return false;

    dropTickOnAllInputs();
    sendTickOnAllOutputs();

    //Follows: the 'handleMessageOn' method for each port.
    handleMessageOnIIn();

    return true;

  }

  protected void handleMessageOnIIn() {
    if (this.iIn.isBufferEmpty()) return;

    if (tryHandleTick()) {
      return; // if the above method returned true, ticks on incoming ports were dropped and this method has already been called again, so we can exit here
    }

    Integer iIn = this.iIn.pollBuffer().getData();

    if (iIn >= 0) {
      Integer gtEq0 = null;

      gtEq0 = iIn;

      this.gtEq0.send(gtEq0);

      return;
    }

    if (iIn < 0) {
      Integer lt0 = null;

      lt0 = iIn;

      this.lt0.send(lt0);

      return;
    }
  }
}
