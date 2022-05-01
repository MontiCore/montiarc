/* (c) https://github.com/MontiCore/monticore */
package dsim.modeautomata

import dsim.conf.*
import dsim.port.IDataSource

class ModeAutomaton<T : IGuardInterface>(private val guardVariables: T) : IModeAutomaton<T> {
  private val _modes: MutableMap<String, IReconfiguration> = mutableMapOf()

  private val _transitions: MutableList<Transition<T>> = mutableListOf()

  private var _currentMode: IReconfiguration = FunctionalReconfiguration("no config") {}

  override val currentMode get() = _currentMode

  /**
   * Add a mode (internally saved configuration) to this component.
   * If more than one name is given, identical modes with respective names are added.
   * If [initial] is true, the mode is set as initial mode (active immediately after construction).
   */
  override fun addMode(vararg names: String, initial: Boolean, changeScript: ChangeScript) {
    names.forEach { name ->
      FunctionalReconfiguration(name, changeScript).also {
        _modes[name] = it
        if (initial) {
          _currentMode = it
        }
      }
    }
  }

  override fun addTransition(fromModeName: String?, targetModeName: String, trigger:Collection<IDataSource>, reaction: ChangeScript, condition: T.() -> Boolean) {
    if (targetModeName !in _modes.keys) throw NoSuchModeException(targetModeName)
    _transitions += Transition(fromModeName, targetModeName, condition, reaction, trigger)
  }

  override fun update(ports: Set<IDataSource>): IReconfiguration {
    val toTake = _transitions
            .filter { t -> t.source?.equals(_currentMode.name) ?: true }
            .filter { t -> ports.containsAll(t.ports) }
            .firstOrNull { t -> t.guard(guardVariables) }
            ?: return EmptyReconfiguration(_currentMode.name)
    _currentMode = _modes[toTake.target] ?: throw NoSuchModeException(toTake.target)
    return _currentMode.also { toTake.reaction }
  }
}

class NoSuchModeException(mode: String) : Exception(mode)
