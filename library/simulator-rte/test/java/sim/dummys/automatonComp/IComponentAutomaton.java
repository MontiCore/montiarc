/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.automatonComp;

import sim.comp.ITimedComponent;
import sim.port.IInPort;
import sim.port.IOutPort;
import sim.port.IPort;

public interface IComponentAutomaton extends ITimedComponent {

  public IOutPort<Integer> getOut();

  public void setOut(IPort<Integer> out);

  public IInPort<Integer> getIn();

}
