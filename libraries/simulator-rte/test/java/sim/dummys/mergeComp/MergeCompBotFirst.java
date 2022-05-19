/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import sim.comp.ATimedComponent;
import sim.error.ISimulationErrorHandler;
import sim.message.Message;
import sim.port.IForwardPort;
import sim.port.IInPort;
import sim.port.IOutPort;
import sim.port.IPort;
import sim.sched.IScheduler;
import sim.serialiser.BackTrackHandler;

public class MergeCompBotFirst extends ATimedComponent implements IMergeComp {

  private IForwardPort<Integer> in;

  private IBottomComp bottom;

  private ITopComp top;

  private IFinalComp merge;

  public IInPort<Integer> getin() {
    return in;
  }

  @Override
  public void setup(IScheduler scheduler, ISimulationErrorHandler errorHandler, BackTrackHandler backTrackHandler) {
    //setup own prots
    in = scheduler.createForwardPort();
    in.setup(this, scheduler);

    setBth(backTrackHandler);
    setErrorHandler(errorHandler);
    setScheduler(scheduler);
    setComponentName("Merge");

    //create components
    bottom = new BottomCompAut();
    bottom.setup(scheduler, errorHandler, backTrackHandler);

    top = new TopCompAut();
    top.setup(scheduler, errorHandler, backTrackHandler);

    merge = new FinalCompAut();
    merge.setup(scheduler, errorHandler, backTrackHandler);

    //connectors
    in.add(bottom.getbIn());
    in.add(top.gettIn());

    bottom.setbOut((IPort) merge.getmInBool());
    top.settOut((IPort) merge.getmInInt());
  }

  public IOutPort<Integer> getout() {
    return merge.getmOut();
  }

  @Override
  protected void timeStep() {

  }

  @Override
  public void checkConstraints() {

  }

  @Override
  public void handleMessage(IInPort<?> port, Message<?> message) {

  }

  @Override
  public void handleTick() {

  }
}
