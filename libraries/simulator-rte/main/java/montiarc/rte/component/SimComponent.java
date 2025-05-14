/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import montiarc.rte.port.InPort;
import montiarc.rte.port.NoMsgType;

import java.util.List;

/**
 * Reflects the view of a scheduler on a component
 */
public interface SimComponent extends Component {

  default boolean hasModeAutomaton() { return false; }

  List<? extends SimComponent> getAllSubcomponents();

  List<? extends InPort<?>> getAllInPorts();

  List<? extends InPort<?>> getAllSyncedInPorts();

  List<? extends InPort<?>> getAllMsgEventInPorts();

  /**
   * Executes the component's logic that processes the completion of a timing interval.
   * <br>
   * This does not mean that, if the component is decomposed, all sub components process the tick as well,
   * as this is scheduling-dependent.
   */
  void handleTick();

  void handleMessage(InPort<?> p);

  void init();

  void unregisterFromScheduler();
}
