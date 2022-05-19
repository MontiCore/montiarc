/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import sim.comp.ITimedComponent;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;

public interface IFinalComp extends ITimedComponent {

  public IInSimPort<Integer> getmInInt();

  public IInSimPort<Boolean> getmInBool();

  public IOutSimPort<Integer> getmOut();

  void setmInInt(IInSimPort<Integer> mInInt);

  void setmInBool(IInSimPort<Boolean> mInBool);

  void setmOut(IOutSimPort<Integer> mOut);
}
