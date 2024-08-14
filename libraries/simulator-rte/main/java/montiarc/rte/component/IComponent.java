/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import montiarc.rte.port.IInPort;
import montiarc.rte.port.TickPort;

import java.util.List;


public interface IComponent {

  String getName();

  default boolean hasModeAutomaton() { return false; }

  List<? extends IComponent> getAllSubcomponents();

  void init();

  /**
   * Executes the component's logic that processes the completion of a timing interval.
   * <br>
   * This does not mean that, if the component is decomposed, all sub components process the tick as well.
   */
  void handleTick();

  void handleMessage(IInPort<?> p);
  /**
   * Simulation-internal port that is used to control the time progress of components.
   */
  TickPort getTickPort();
}
