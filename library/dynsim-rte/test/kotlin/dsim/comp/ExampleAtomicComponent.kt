/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.log.log
import dsim.msg.Message
import dsim.port.util.port
import dsim.sched.atomic.untimedSchedule
import kotlinx.coroutines.flow.collect

/**
 * Exemplary component:
 * Receives String type messages at port "oof".
 * Upon update, takes one Message, reverses the string and outputs the same message
 * on port "foo".
 */
class ExampleAtomicComponent(name: String) : AComponent(name, inPorts = setOf(port<String>("oof")),
    outPorts = setOf(port<String>("foo"))) {

  override suspend fun behavior() {
    untimedSchedule(inputPorts).collect { event ->
      if (event.port == this.getInputPort("oof")) {
        val pld = event.msg.payload
        if (pld is String) {
          log("received $pld")
          getOutputPort("foo").pushMsg(Message(pld.reversed()))
          log("sent ${pld.reversed()}")
        }
      }
    }
  }
}
