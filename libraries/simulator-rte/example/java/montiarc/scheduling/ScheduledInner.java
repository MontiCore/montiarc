/* (c) https://github.com/MontiCore/monticore */
package montiarc.scheduling;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;
import montiarc.rte.scheduling.Computation;
import montiarc.rte.scheduling.ISchedule;

import java.util.List;

public class ScheduledInner implements ITimedComponent {

  protected final String qualifiedInstanceName;

  @Override
  public String getQualifiedInstanceName() {
    return qualifiedInstanceName;
  }

  @Override
  public List<TimeAwareInPort<?>> getAllInPorts() {
    return List.of(trigger);
  }

  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of(output);
  }

  ISchedule scheduler;
  StringBuilder trace;
  String instanceName;

  public ScheduledInner(String qualifiedInstanceName, ISchedule scheduler,
                        StringBuilder trace, String instanceName) { // last 2 parameters are from the model, first 2 are "default"
    this.qualifiedInstanceName = qualifiedInstanceName;
    this.scheduler = scheduler;
    this.trace = trace;
    this.instanceName = instanceName;
  }

  TimeAwareInPort<Boolean> trigger = new TimeAwareInPort<Boolean>(getQualifiedInstanceName() + ".trigger") {
    @Override
    protected void handleBuffer() {
      if (buffer.isEmpty()) return;

      handleMessageOnTrigger();
    }
  };

  TimeAwareOutPort<Boolean> output = new TimeAwareOutPort<>(getQualifiedInstanceName() + ".output");

  protected boolean areAllInputsTickBlocked() { // this method could be generated for all ports with time-aware input ports
    return this.trigger.isTickBlocked();
  }

  protected void dropTickOnAllInputs() { // this method could be generated for all ports with time-aware input ports
    this.trigger.dropBlockingTick();
  }

  protected void sendTickOnAllOutputs() { // this method could be generated for all ports with time-aware output ports
    this.output.sendTick();
  }

  protected boolean tryHandleTick() { // this method could be generated for all ports with time-aware input ports
    if (!areAllInputsTickBlocked()) return false;

    dropTickOnAllInputs();
    sendTickOnAllOutputs();

    //Follows: the 'handleMessageOn' method for each port.
    handleMessageOnTrigger();

    return true;
  }

  protected void handleMessageOnTrigger() {
    if (this.trigger.isBufferEmpty()) return;

    if (tryHandleTick()) return;

    //set uo runnable to be passed to the scheduler
    Runnable computationRunnable = () -> {
      //Shadowing variables setup
      Boolean trigger = this.trigger.pollBuffer().getValue();

      //compute block, NOT taken directly from the model - output port assignments are transformed to send data directly
      trace.append(instanceName + "1");
      output.send(trigger);
      trace.append(instanceName + "2");
    };

    //register computation with scheduler
    this.scheduler.registerComputation(new Computation(this, computationRunnable));
  }
}
