/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.IComponent;

public interface IComputation {

  void run();

  IComponent getOwner();
}
