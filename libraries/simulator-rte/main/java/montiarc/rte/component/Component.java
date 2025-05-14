/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import montiarc.rte.port.InOutPort;
import montiarc.rte.port.InPort;
import montiarc.rte.port.NoMsgType;

import java.util.List;


public interface Component {

  String getName();

  default boolean hasModeAutomaton() { return false; }

  List<? extends Component> getAllSubcomponents();

  List<InOutPort<?, ?>> getAllInPorts();

  @Deprecated(forRemoval = true)
  void init();

  /**
   * Executes the component's logic that processes the completion of a timing interval.
   * <br>
   * This does not mean that, if the component is decomposed, all sub components process the tick as well,
   * as this is scheduling-dependent.
   */
  void handleTick();

  void handleMessage(InPort<?> p);
}
