/* (c) https://github.com/MontiCore/monticore */
package dsim.modeautomata

import dsim.comp.IReconfigurable
import dsim.conf.ChangeScript
import dsim.conf.IReconfiguration
import dsim.port.IDataSink
import dsim.port.IDataSource

interface IModeAutomaton<T : IGuardInterface> {
  /**
   * performs one calculation step of the automaton
   * @param ports input ports that received a message (which triggered the update), or null, if the message was not a single message event
   * @return the reconfiguration to take or an empty change-script, if the mode was not changed
   */
  fun update(ports: Set<IDataSource> = emptySet()): IReconfiguration

  /**
   * the current mode
   */
  val currentMode: IReconfiguration

  /**
   * adds a mode to the automaton
   * @param initial if the mode is initial (default is false)
   * @param names name for the mode (if multiple names are given, multiple similar modes are created)
   * @param changeScript a delta that describes a component's transition from any mode to this mode
   */
  fun addMode(vararg names: String, initial: Boolean = false, changeScript: ChangeScript)

  /**
   * adds a transition to the automaton
   * @param fromModeName name of the source mode, or null if the transition may start anywhere
   * @param targetModeName name of the mode, that is taken after transitioning
   * @param reaction additional changes to apply
   * @param trigger port, whose message-receipt triggers this transition
   * @param condition the guard of the transition
   */
  fun addTransition(fromModeName: String? = null, targetModeName: String, trigger: Collection<IDataSource> = emptySet(), reaction: ChangeScript = {}, condition: T.() -> Boolean)
}
