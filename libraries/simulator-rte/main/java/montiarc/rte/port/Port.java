/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.Component;

public interface Port {
  
  String getQualifiedName();

  Component getOwner();
}
