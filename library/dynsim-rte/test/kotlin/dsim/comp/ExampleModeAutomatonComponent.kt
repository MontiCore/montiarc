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

class ExampleModeAutomatonComponent(name: String) : ADecomposedComponent(name) {
  private val transitionTrigger = object : IGuardInterface {
    override val inputPorts get() = this@ExampleModeAutomatonComponent.inputPorts
    override val subcomponents get() = this@ExampleModeAutomatonComponent.subcomponents

    override var lastEvent: SingleMessageEvent? = null
  }

  private val modeAutomaton = ModeAutomaton(transitionTrigger)

  init {
    addInPort(port<String>("input"))
    addInPort(port<String>("switch"))
    addOutPort(port<String>("output1"))
    addOutPort(port<String>("output2"))

    modeAutomaton.addMode("mode A", initial = true) {
      disconnectAll()
      connect(getInputPort("input"), getOutputPort("output1"))
    }
    modeAutomaton.addMode("mode B") {
      disconnectAll()
      connect(getInputPort("input"), getOutputPort("output2"))
    }

    modeAutomaton.addTransition("mode B", "mode A") {
      lastEvent?.port == getInputPort("switch") &&
          lastEvent?.msg?.payload == "switch to A"
    }

    modeAutomaton.addTransition("mode A", "mode B") {
      lastEvent?.port == getInputPort("switch") &&
          lastEvent?.msg?.payload == "switch to B"
    }
    reconfigure(modeAutomaton.currentMode)
  }

  /**
   * Transition trigger is updated with new event and potential reconfiguration
   * takes place BEFORE message is forwarded.
   */
  override suspend fun behavior() {
    untimedSchedule(inputPorts).collect { event ->
      when (event) {
        is SingleMessageEvent -> {
          transitionTrigger.lastEvent = event
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
