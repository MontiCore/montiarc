/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import sim.comp.ITimedComponent;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;

public interface IBottomComp extends ITimedComponent {

  IOutSimPort<Boolean> getbOut();

  IInSimPort<Integer> getbIn();

  void setbIn(IInSimPort<Integer> bIn);

  void setbOut(IOutSimPort<Boolean> bOut);
}
