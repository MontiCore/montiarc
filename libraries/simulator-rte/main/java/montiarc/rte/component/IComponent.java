/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import montiarc.rte.port.AbstractInPort;

public interface IComponent {

  String getName();

  void handleMessage(AbstractInPort<?> receivingPort);
}
