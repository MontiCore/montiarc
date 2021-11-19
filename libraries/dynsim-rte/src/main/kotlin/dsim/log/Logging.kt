/* (c) https://github.com/MontiCore/monticore */
package dsim.log

import dsim.comp.IComponent
import dsim.port.IPort
import dsim.sim.ISimulation

// To be thoroughly expanded
fun log(entry: String) {
  println(entry)
}

interface ILoggable

object Logging {
  var mutedComponents: Boolean = false
  var mutedPorts: Boolean = false
  var mutedSimulation: Boolean = false

  private val muted: MutableSet<ILoggable> = mutableSetOf()

  fun muted(part: ILoggable): Boolean {
    if (part in muted) return true

    when (part) {
      is IComponent -> return mutedComponents
      is IPort -> return mutedPorts
      is ISimulation -> return mutedSimulation
    }
    return false
  }

  fun mute(part: ILoggable) = muted.add(part)
  fun unmute(part: ILoggable) = muted.remove(part)
}

fun ILoggable.log(entry: String) {
  if (!Logging.muted(this)) {
    println("[$this]: $entry")
  }
}
