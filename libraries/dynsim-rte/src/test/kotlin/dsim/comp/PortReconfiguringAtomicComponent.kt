/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.msg.Message
import dsim.port.util.port
import dsim.sched.atomic.untimedSchedule
import kotlinx.coroutines.flow.collect

class PortReconfiguringAtomicComponent(name: String) : AComponent(name,
    inPorts = setOf(port<String>("input main")), outPorts = setOf(port<Int>("counter"))) {
  private var inPortCounter = 1

  override suspend fun behavior() {
    untimedSchedule(inputPorts).collect { event ->
      when (event.msg.payload) {
        "add input" -> {
          inPortCounter++
          addInPort(port<String>("input 2"))
        }
        "get count" -> {
          getOutputPort("counter").pushMsg(Message(inPortCounter))
        }
      }
    }
  }
}
