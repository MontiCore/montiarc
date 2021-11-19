/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.msg.Message
import dsim.port.util.port
import dsim.sched.atomic.untimedSchedule
import kotlinx.coroutines.flow.collect

class StreamBundleAtomicComponent(name: String) : AComponent(name,
    inPorts = setOf(port<String>("a"), port<String>("b"), port<Int>("c")),
    outPorts = setOf(port<String>("x"), port<Int>("y"))) {

  private var counter = 0

  override suspend fun behavior() {
    untimedSchedule(inputPorts).collect { event ->

      counter++

      val pld = event.msg.payload

      when (event.port) {
        getInputPort("a") -> getOutputPort("x").pushMsg(Message(pld)) // Send through to port x
        getInputPort("b") -> getOutputPort("x").pushMsg(Message(pld)) // Send through to port x
        getInputPort("c") -> { // Reset counter to received value
          if (pld is Int) {
            counter = pld
            getOutputPort("x").pushMsg(Message(pld.toString()))
          }
        }
      }
      getOutputPort("y").pushMsg(Message(counter))
    }
  }
}
