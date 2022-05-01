/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.port.util.port
import dsim.sched.atomic.untimedSchedule
import kotlinx.coroutines.flow.collect

class FunctionalScheduledDynamicAtomicComponent(name: String) : AComponent(name,
    setOf(port<String>("input")), setOf(port<String>("output"))) {

  override suspend fun behavior() {
    untimedSchedule(inputPorts).collect { event ->
      if (event.msg.payload == "new input") {
        addInPort(port<String>("new input"))
      } else {
        getOutputPort("output").pushMsg(event.msg)
      }
    }
  }
}
