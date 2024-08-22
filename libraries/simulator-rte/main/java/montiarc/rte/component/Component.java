/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import montiarc.rte.port.InPort;
import montiarc.rte.port.NoMsgType;

import java.util.List;


public interface Component {

  String getName();

  default boolean hasModeAutomaton() { return false; }

  List<? extends Component> getAllSubcomponents();

  void init();

  /**
   * Executes the component's logic that processes the completion of a timing interval.
   * <br>
   * This does not mean that, if the component is decomposed, all sub components process the tick as well.
   */
  void handleTick();

  void handleMessage(InPort<?> p);
  /**
   * Simulation-internal port that is used to control the time progress of components.
   */
  InPort<NoMsgType> getTickPort();
}
