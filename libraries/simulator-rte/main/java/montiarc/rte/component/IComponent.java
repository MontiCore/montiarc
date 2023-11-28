/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import montiarc.rte.port.IInPort;

public interface IComponent {

  String getName();

  void handleMessage(IInPort<?> receivingPort);
}
