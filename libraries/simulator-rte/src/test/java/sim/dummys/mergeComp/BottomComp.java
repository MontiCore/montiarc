/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import sim.sched.IScheduler;
import sim.error.ISimulationErrorHandler;
import sim.comp.ATimedComponent;
import sim.message.Message;
import sim.message.Tick;
import sim.port.IInPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;

public abstract class BottomComp extends ATimedComponent implements IBottomComp {

  protected IInSimPort<Integer> bIn;

  protected IOutSimPort<Boolean> bOut;

  @Override
  public void checkConstraints() {
    // TODO Auto-generated method stub
  }

  @Override
  public void setup(IScheduler s, ISimulationErrorHandler eh) {
    setScheduler(s);
    setErrorHandler(eh);
    bIn = s.createInPort();
    bIn.setup(this, s);
    bOut = s.createOutPort();
  }

  public IInSimPort<Integer> getbIn() {
    return bIn;
  }

  public IOutSimPort<Boolean> getbOut() {
    return bOut;
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

  public void sendbOut(Boolean message) {
    bOut.send(Message.of(message));
  }

  public abstract void treatbIn(Integer msg);
}
