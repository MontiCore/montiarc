/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.port.util.port
import dsim.sched.atomic.untimedSchedule
import kotlinx.coroutines.flow.collect

class FunctionalScheduledAtomicComponent(name: String) : AComponent(name,
    setOf(port<String>("input")), setOf(port<String>("output"))) {

  override suspend fun behavior() {
    untimedSchedule(inputPorts).collect { event ->
      getOutputPort("output").pushMsg(event.msg)
    }
  }
}
