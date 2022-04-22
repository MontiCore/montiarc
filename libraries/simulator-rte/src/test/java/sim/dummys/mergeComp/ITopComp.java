/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import sim.comp.ITimedComponent;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;

public interface ITopComp extends ITimedComponent {

  public IInSimPort<Integer> gettIn();

  public IOutSimPort<Integer> gettOut();
}
