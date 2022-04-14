/* (c) https://github.com/MontiCore/monticore */
package sim.dummys;

import sim.IScheduler;
import sim.error.ISimulationErrorHandler;
import sim.generic.ATimedComponent;
import sim.generic.Message;
import sim.generic.Tick;
import sim.port.*;

public abstract class ComponentAutomaton extends ATimedComponent implements IComponentAutomaton {

  private IInSimPort<Integer> in;

  private IOutSimPort<Integer> out;

  public IInPort<Integer> getIn() {
    return this.in;
  }

  public IOutPort<Integer> getOut() {
    return this.out;
  }

  public void setOut(IPort<Integer> out) {
    if (this.out == null) {
      this.out = out;
    } else {
      this.out.addReceiver(out);
    }
  }

  @Override
  public void setup(IScheduler s, ISimulationErrorHandler eh) {
    in = new Port<Integer>();
    out = new TestPort<Integer>();
    ((IInSimPort<Integer>) in).setup(this, s);
  }

  public void handleMessage(IInPort<?> port, Message<?> message) {
    if (port == in) {
      treatIn(message);
    }
  }

  public void InProcessed(Message<Integer> m) {
    out.send(m);
  }

  protected abstract void treatIn(Message<?> m);

  @Override
  protected void timeStep() {

  }

  @Override
  public void checkConstraints() {

  }

  @Override
  public void handleTick() {
    out.send(Tick.<Integer>get());
    incLocalTime();
  }
}
