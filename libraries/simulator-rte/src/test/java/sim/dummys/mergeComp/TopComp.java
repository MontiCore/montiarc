/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import sim.IScheduler;
import sim.error.ISimulationErrorHandler;
import sim.generic.ATimedComponent;
import sim.generic.Message;
import sim.generic.Tick;
import sim.port.IInPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;

public abstract class TopComp extends ATimedComponent implements ITopComp {

  protected IInSimPort<Integer> tIn;

  protected IOutSimPort<Integer> tOut;

  @Override
  public void checkConstraints() {
    // TODO Auto-generated method stub
  }

  @Override
  public void setup(IScheduler s, ISimulationErrorHandler eh) {
    setScheduler(s);
    setErrorHandler(eh);
    tIn = s.createInPort();
    tIn.setup(this, s);
    tOut = s.createOutPort();
  }

  public IInSimPort<Integer> gettIn() {
    return tIn;
  }

  public IOutSimPort<Integer> gettOut() {
    return tOut;
  }

  @Override
  public void handleMessage(IInPort p, Message m) {
    if (p == tIn) {
      treattIn((Integer) m.getData());
    }
  }

  @Override
  public void handleTick() {
    tOut.send(Tick.get());
    incLocalTime();
  }

  public void sendtOut(Integer message) {
    tOut.send(Message.of(message));
  }

  public abstract void treattIn(Integer msg);
}
