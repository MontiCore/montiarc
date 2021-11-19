/* (c) https://github.com/MontiCore/monticore */
package dsim.modeautomata

import dsim.conf.ChangeScript
import dsim.conf.IReconfiguration

interface IModeAutomaton<T : ITransitionTrigger> {
  fun update(): IReconfiguration
  val currentMode: IReconfiguration

  fun mode(vararg names: String, changeScript: ChangeScript)
  fun mode(initial: Boolean, vararg names: String, changeScript: ChangeScript)
  fun transition(fromModeName: String? = null, targetModeName: String, condition: T.() -> Boolean)
}
