/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import sim.IScheduler;
import sim.error.ISimulationErrorHandler;
import sim.generic.ATimedComponent;
import sim.generic.Message;
import sim.port.*;

public class MergeCompBotFirst extends ATimedComponent implements IMergeComp {

  private IForwardPort<Integer> in;

  private IBottomComp bottom;

  private ITopComp top;

  private IFinalComp merge;

  public IInPort<Integer> getin() {
    return in;
  }

  @Override
  public void setup(IScheduler scheduler, ISimulationErrorHandler errorHandler) {
    //setup own prots
    in = scheduler.createForwardPort();
    in.setup(this, scheduler);

    //create components
    bottom = new BottomCompAut();
    bottom.setup(scheduler, errorHandler);

    top = new TopCompAut();
    top.setup(scheduler, errorHandler);

    merge = new FinalCompAut();
    merge.setup(scheduler, errorHandler);

    //connectors
    in.add(bottom.getbIn());
    in.add(top.gettIn());

    top.gettOut().addReceiver(merge.getmInInt());
    bottom.getbOut().addReceiver(merge.getmInBool());
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
