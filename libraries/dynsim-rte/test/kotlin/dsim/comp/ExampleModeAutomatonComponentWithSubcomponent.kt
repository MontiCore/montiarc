/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.modeautomata.IGuardInterface
import dsim.modeautomata.ModeAutomaton
import dsim.port.util.port
import dsim.sched.util.SingleMessageEvent
import dsim.sched.decomposed.untimedSchedule
import dsim.sched.util.ForwardEvent
import dsim.sched.util.TickEvent
import kotlinx.coroutines.flow.collect

class ExampleModeAutomatonComponentWithSubcomponent(name: String) : ADecomposedComponent(name) {

  private val trigger = object : IGuardInterface {
    override val inputPorts get() = this@ExampleModeAutomatonComponentWithSubcomponent.inputPorts
    override val subcomponents get() = this@ExampleModeAutomatonComponentWithSubcomponent.subcomponents

    override var lastEvent: SingleMessageEvent? = null
  }

  private val modeAutomaton = ModeAutomaton(trigger)


  init {
    addInPort(port<String>("input"))
    addInPort(port<String>("com"))
    addOutPort(port<String>("output"))

    component(ExampleAtomicComponent("inverter"))

    modeAutomaton.addMode("invert", initial = true) {
      disconnectAll()

      connect(getInputPort("input"), getSubcomponent("inverter").getInputPort("oof"))
      connect(getSubcomponent("inverter").getOutputPort("foo"), getOutputPort("output"))
    }

    modeAutomaton.addMode("pass") {
      disconnectAll()

      connect(getInputPort("input"), getOutputPort("output"))
    }

    modeAutomaton.addTransition("invert", "pass") {
      lastEvent?.port == getInputPort("com") && lastEvent?.msg?.payload == "pass"
    }

    modeAutomaton.addTransition("pass", "invert") {
      lastEvent?.port == getInputPort("com") && lastEvent?.msg?.payload == "invert"
    }
    reconfigure(modeAutomaton.currentMode)
  }

  override suspend fun behavior() {
    untimedSchedule(inputPorts, subcomponents).collect { event ->
      when (event) {
        is SingleMessageEvent -> {
          trigger.lastEvent = event
          reconfigure(modeAutomaton.update())
        }
        is TickEvent -> {
          tickOutputs()
        }
        is ForwardEvent -> {
          forward(event.port, event.msg)
        }
      }
    }
  }
}
