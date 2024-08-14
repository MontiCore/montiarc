/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;

public interface IPort {
  
  String getQualifiedName();

  IComponent getOwner();
}
