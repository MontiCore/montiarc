/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.log.log
import dsim.modeautomata.IGuardInterface
import dsim.modeautomata.ModeAutomaton
import dsim.msg.Message
import dsim.port.IDataSource
import dsim.port.Port
import dsim.sched.util.SingleMessageEvent
import dsim.sched.decomposed.untimedSchedule as dcUntimedSchedule
import dsim.sched.atomic.untimedSchedule as atUntimedSchedule
import dsim.sched.util.ForwardEvent
import dsim.sched.util.TickEvent
import kotlinx.coroutines.flow.collect

class DecomposedComponentWithPortReconfiguringSubcomponent(name: String) : ADecomposedComponent(name) {
  private val trigger = object: IGuardInterface {
    override val subcomponents: Set<ISubcomponentForTransition>
      get() = this@DecomposedComponentWithPortReconfiguringSubcomponent.subcomponents
    override val inputPorts: Set<IDataSource>
      get() = this@DecomposedComponentWithPortReconfiguringSubcomponent.inputPorts
    override var lastEvent: SingleMessageEvent? = null
  }

  private val modeAutomaton = ModeAutomaton(trigger)

  init {
    addInPort(Port.make<String>("input"))
    addOutPort(Port.make<String>("output"))

    component(PortReconfiguringSubcomponent("prsc"))

    modeAutomaton.addMode("default", initial = true) {
      disconnectAll()
      connect(getInputPort("input"), getSubcomponent("prsc").getInputPort("input"))
    }

    modeAutomaton.addMode("id") {
      log("this should happen")
      disconnectAll()
      connect(getInputPort("input"), getSubcomponent("prsc").getInputPort("input"))
      connect(getSubcomponent("prsc").getOutputPort("id out"), getOutputPort("output"))
    }

    modeAutomaton.addTransition("id", "default") {
      lastEvent?.port == getSubcomponent("prsc").getOutputPort("log out") &&
          lastEvent?.msg == Message("removed id port")
    }

    modeAutomaton.addTransition("default", "id") {
      lastEvent?.port == getSubcomponent("prsc").getOutputPort("log out") &&
          lastEvent?.msg?.payload == "added id port"
    }
  }

  override suspend fun behavior() {
    reconfigure(modeAutomaton.currentMode)

    dcUntimedSchedule(inputPorts, subcomponents).collect { event ->
      when(event) {
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

class PortReconfiguringSubcomponent(name: String) : AComponent(name) {
  init {
    addInPort(Port.make<String>("input"))
    addOutPort(Port.make<String>("log out"))
  }

  private var idPortExists = false

  override suspend fun behavior() {
    atUntimedSchedule(inputPorts).collect { event ->

      when (event.msg.payload) {
        "add id port" -> {
          if (!idPortExists) {
            addOutPort(Port.make<String>("id out"))
            getOutputPort("log out").pushMsg(Message("added id port"))
            idPortExists = true
          }
        }

        "remove id port" -> {
          if (idPortExists) {
            removeOutPort(getOutputPort("id out"))
            getOutputPort("log out").pushMsg(Message("removed id port"))
            idPortExists = false
          }
        }

        else -> {
          getOutputPort("log out").pushMsg(Message("received message"))
          if (idPortExists) getOutputPort("id out").pushMsg(event.msg)
        }
      }
    }
  }
}
