/* (c) https://github.com/MontiCore/monticore */
package example.ilc

import dsim.comp.ADecomposedComponent
import dsim.comp.ISubcomponentForTransition
import dsim.modeautomata.IGuardInterface
import dsim.modeautomata.ModeAutomaton
import dsim.port.IDataSource
import dsim.port.Port
import dsim.sched.decomposed.syncSchedule
import dsim.sched.util.ForwardEvent
import dsim.sched.util.SyncEvent
import dsim.sched.util.TickEvent
import kotlinx.coroutines.flow.collect

import example.ilc.signals.*

class LightCtrl(name: String) : ADecomposedComponent(name) {

  // Mode Automaton
  private val trigger = object : IGuardInterface {
    override val subcomponents: Set<ISubcomponentForTransition>
      get() = this@LightCtrl.subcomponents
    override val inputPorts: Set<IDataSource>
      get() = this@LightCtrl.inputPorts
    override var lastEvent: SyncEvent? = null
  }
  private val modeAut = ModeAutomaton(trigger)

  // Architecture Description
  init {
    addInPort(Port.make<ModeCmd>("modeCmd"))

    addOutPort(Port.make<LightCmd>("cmd"))

    create(Arbiter("arbiter"))

    modeAut.addMode("Default", initial = true) {
      // cleanup
      subcomponents.filter { it.name !in setOf("doorEval", "motorEval", "arbiter") }
          .forEach { delete(it) }
      inputPorts.filter { it.name !in setOf("doorStatus", "modeCmd") }
          .forEach { removeInPort(it) }
      disconnectAll()

      addInPort(Port.make<DoorStatus>("doorStatus"))

      // reconfigure into mode
      create(DoorEval("doorEval"))

      connect(getInputPort("doorStatus"), getSubcomponent("doorEval").getInputPort("status"))
      connect(
          getSubcomponent("doorEval").getOutputPort("request"),
          getSubcomponent("arbiter").getInputPort("request")
      )
      connect(getSubcomponent("arbiter").getOutputPort("lightCmd"), getOutputPort("cmd"))
    }

    modeAut.addMode("Comfort") {
      subcomponents.filter { it.name !in setOf("motorEval", "arbiter") }
          .forEach { delete(it) }
      inputPorts.filter { it.name !in setOf("motorStatus", "modeCmd") }
          .forEach { removeInPort(it) }
      disconnectAll()

      addInPort(Port.make<MotorStatus>("motorStatus"))

      create(MotorEval("motorEval"))

      connect(getInputPort("motorStatus"), getSubcomponent("motorEval").getInputPort("status"))
      connect(
          getSubcomponent("motorEval").getOutputPort("request"),
          getSubcomponent("arbiter").getInputPort("request")
      )
      connect(getSubcomponent("arbiter").getOutputPort("lightCmd"), getOutputPort("cmd"))
    }

    modeAut.addTransition("Default", "Comfort") {
      lastEvent?.get(getInputPort("modeCmd"))?.payload == ModeCmd.COMFORT
    }
    modeAut.addTransition("Comfort", "Default") {
      lastEvent?.get(getInputPort("modeCmd"))?.payload == ModeCmd.DEFAULT
    }

    reconfigure(modeAut.currentMode)

  }

  // Behavior, consuming and producing messages
  override suspend fun behavior() {
    syncSchedule(inputPorts, subcomponents).collect { event ->
      when (event) {
        is ForwardEvent -> forward(event)
        is SyncEvent -> {
          trigger.lastEvent = event
          reconfigure(modeAut.update())
        }
        is TickEvent -> {
          tickOutputs()
        }
      }
    }
  }
}
