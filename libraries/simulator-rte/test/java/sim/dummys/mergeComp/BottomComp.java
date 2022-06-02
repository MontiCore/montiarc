/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import sim.comp.ATimedComponent;
import sim.error.ISimulationErrorHandler;
import sim.message.Message;
import sim.message.Tick;
import sim.message.TickedMessage;
import sim.port.IInPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;
import sim.sched.IScheduler;
import sim.serialiser.BackTrackHandler;

public abstract class BottomComp extends ATimedComponent implements IBottomComp {

  protected IInSimPort<Integer> bIn;

  protected IOutSimPort<Boolean> bOut;

  @Override
  public void checkConstraints() {
    // TODO Auto-generated method stub
  }

  @Override
  public void setup(IScheduler s, ISimulationErrorHandler eh, BackTrackHandler backTrackHandler) {
    setScheduler(s);
    setErrorHandler(eh);
    bIn = s.createInPort();
    bIn.setup(this, s);
    setBth(backTrackHandler);
    setComponentName("Bottom");
  }

  public IInSimPort<Integer> getbIn() {
    return bIn;
  }

  public IOutSimPort<Boolean> getbOut() {
    return bOut;
  }

  @Override
  public void setbIn(IInSimPort<Integer> bIn) {
    this.bIn = bIn;
  }

  @Override
  public void setbOut(IOutSimPort<Boolean> bOut) {
    this.bOut = bOut;
  }

  @Override
  public void handleMessage(IInPort p, Message m) {
    if (p == bIn) {
      treatbIn((Integer) m.getData());
    }
  }

  @Override
  public void handleTick() {
    bOut.send(Tick.get());
    incLocalTime();
  }

  public void sendbOut(TickedMessage message) {
    bOut.send(message);
  }

  public abstract void treatbIn(Integer msg);
}
