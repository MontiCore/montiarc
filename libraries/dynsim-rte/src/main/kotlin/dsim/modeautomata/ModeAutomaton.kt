/* (c) https://github.com/MontiCore/monticore */
package dsim.modeautomata

import dsim.conf.*

class ModeAutomaton<T : ITransitionTrigger>(private val trigger: T) : IModeAutomaton<T> {
  private val _modes: MutableMap<String, IReconfiguration> = mutableMapOf()

  private val _transitions: MutableMap<Pair<String?, String>, T.() -> Boolean> = mutableMapOf()

  private var _currentMode: IReconfiguration = FunctionalReconfiguration("no config") {}

  override val currentMode get() = _currentMode

  /**
   * Add a mode (internally saved configuration) to this component.
   * If more than one name is given, identical modes with respective names are added.
   * If [initial] is true, the mode is set as initial mode (active immediately after construction).
   */
  override fun mode(initial: Boolean, vararg names: String, changeScript: ChangeScript) {
    names.forEach { name ->
      FunctionalReconfiguration(name, changeScript).also {
        _modes[name] = it
        if (initial) {
          _currentMode = it
        }
      }
    }
  }

  /**
   * Overload of mode, initial assumed to be false.
   */
  override fun mode(vararg names: String, changeScript: ChangeScript) {
    mode(initial = false, changeScript = changeScript, names = names)
  }

  override fun transition(fromModeName: String?, targetModeName: String, condition: T.() -> Boolean) {
    if (targetModeName !in _modes.keys) throw NoSuchModeException()
    _transitions[fromModeName to targetModeName] = condition
  }

  override fun update(): IReconfiguration {
    _transitions.filter { (modes, _) -> modes.first?.equals(_currentMode.name) ?: true }
        .forEach { (modes, condition) ->
          if (trigger.condition()) {
            _currentMode = _modes[modes.second] ?: throw NoSuchModeException()
            return _currentMode
          }
        }
    return _currentMode
  }
}

class NoSuchModeException : Exception()
