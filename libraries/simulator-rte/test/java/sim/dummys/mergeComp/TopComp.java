/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import sim.comp.ATimedComponent;
import sim.error.ISimulationErrorHandler;
import sim.message.Message;
import sim.message.Tick;
import sim.port.IInPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;
import sim.sched.IScheduler;
import sim.serialiser.BackTrackHandler;

public abstract class TopComp extends ATimedComponent implements ITopComp {

  protected IInSimPort<Integer> tIn;

  protected IOutSimPort<Integer> tOut;

  @Override
  public void checkConstraints() {
    // TODO Auto-generated method stub
  }

  @Override
  public void setup(IScheduler s, ISimulationErrorHandler eh, BackTrackHandler backTrackHandler) {
    setScheduler(s);
    setErrorHandler(eh);
    tIn = s.createInPort();
    tIn.setup(this, s);
    setBth(backTrackHandler);
    setComponentName("Top");
  }

  public IInSimPort<Integer> gettIn() {
    return tIn;
  }

  public IOutSimPort<Integer> gettOut() {
    return tOut;
  }

  @Override
  public void settIn(IInSimPort<Integer> tIn) {
    this.tIn = tIn;
  }

  @Override
  public void settOut(IOutSimPort<Integer> tOut) {
    this.tOut = tOut;
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
