/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import montiarc.rte.port.IInPort;

import java.util.List;

public interface ITimedComponent extends IComponent {

  void init();

  void handleTick();

  @Override
  List<ITimedComponent> getAllSubcomponents();

  IInPort<?> getTickPort();
}
