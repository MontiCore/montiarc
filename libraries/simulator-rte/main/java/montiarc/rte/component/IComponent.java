/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import montiarc.rte.port.IInPort;

import java.util.List;


public interface IComponent {

  String getName();

  void handleMessage(IInPort<?> p);

  default boolean hasModeAutomaton() { return false; }

  List<? extends IComponent> getAllSubcomponents();
}
