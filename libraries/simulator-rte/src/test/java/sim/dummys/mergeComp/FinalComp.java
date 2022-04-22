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
import sim.port.TestPort;

public abstract class FinalComp extends ATimedComponent implements IFinalComp {

  protected IInSimPort<Integer> mInInt;

  protected IInSimPort<Boolean> mInBool;

  protected IOutSimPort<Integer> mOut;

  @Override
  public void checkConstraints() {
    // TODO Auto-generated method stub
  }

  @Override
  public void setup(IScheduler s, ISimulationErrorHandler eh) {
    setScheduler(s);
    setErrorHandler(eh);
    mInBool = s.createInPort();
    mInBool.setup(this, s);
    mInInt = s.createInPort();
    mInInt.setup(this, s);
    mOut = new TestPort<Integer>();
  }

  public IInSimPort<Integer> getmInInt() {
    return mInInt;
  }

  public IInSimPort<Boolean> getmInBool() {
    return mInBool;
  }

  public IOutSimPort<Integer> getmOut() {
    return mOut;
  }

  @Override
  public void handleMessage(IInPort p, Message m) {
    if (p == mInInt) {
      treatmInInt((Integer) m.getData());
    }
    if (p == mInBool) {
      treatmInBool((Boolean) m.getData());
    }
  }

  @Override
  public void handleTick() {
    mOut.send(Tick.get());
    incLocalTime();
  }

  public void sendmOut(Integer message) {
    mOut.send(Message.of(message));
  }

  public abstract void treatmInInt(Integer msg);

  public abstract void treatmInBool(Boolean msg);
}
