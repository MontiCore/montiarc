/* (c) https://github.com/MontiCore/monticore */
package dsim.parallel

import dsim.comp.AComponent
import dsim.comp.ADecomposedComponent
import dsim.msg.Message
import dsim.msg.Tick
import dsim.port.Port
import dsim.sched.atomic.syncSchedule
import dsim.sched.util.ForwardEvent
import dsim.sched.decomposed.syncSchedule as dcSyncSchedule
import dsim.sched.util.SyncEvent
import dsim.sched.util.TickEvent
import kotlinx.coroutines.flow.collect

/**
 * This component receives two strings every time slot,
 * counts the number of unique characters in them and adds those numbers together.
 */
class SimpleParallelTimeSyncComponent(name: String) : ADecomposedComponent(name) {
  init {
    addInPort(Port.make<String>("word1"))
    addInPort(Port.make<String>("word2"))

    addOutPort(Port.make<Int>("sum"))

    component(UniqueCharacterCounter("ucc1"))
    component(UniqueCharacterCounter("ucc2"))
    component(Adder("adder"))

    connect(getInputPort("word1"), getSubcomponent("ucc1").getInputPort("word"))
    connect(getInputPort("word2"), getSubcomponent("ucc2").getInputPort("word"))

    connect(getSubcomponent("ucc1").getOutputPort("uniqueCharCount"), getSubcomponent("adder").getInputPort("summand1"))
    connect(getSubcomponent("ucc2").getOutputPort("uniqueCharCount"), getSubcomponent("adder").getInputPort("summand2"))

    connect(getSubcomponent("adder").getOutputPort("sum"), getOutputPort("sum"))
  }

  override suspend fun behavior() {
    dcSyncSchedule(inputPorts, subcomponents).collect { event ->
      when (event) {
        is ForwardEvent -> {
          forward(event.port, event.msg)
        }
        is TickEvent -> {
          tickOutputs()
        }
      }
    }
  }
}

class UniqueCharacterCounter(name: String) : AComponent(name) {
  init {
    addInPort(Port.make<String>("word"))
    addOutPort(Port.make<Int>("uniqueCharCount"))
  }

  override suspend fun behavior() {
    syncSchedule(inputPorts).collect { event ->
      when (event) {
        is TickEvent -> {
          outputPorts.forEach { it.pushMsg(Tick()) }
        }
        is SyncEvent -> {
          (event[getInputPort("word")]?.payload as? String?)?.let { s ->
            val uniqueChars = s.toSortedSet().count()
            repeat(30) {
              fun fib(n: Int): Int {
                if (n == 0) return 1
                if (n == 1) return 1
                return fib(n - 1) + fib(n - 2)
              }
              fib(it)
            }
            getOutputPort("uniqueCharCount").pushMsg(Message(uniqueChars))
          }
        }
      }
    }
  }
}

class Adder(name: String) : AComponent(name) {
  init {
    addInPort(Port.make<Int>("summand1"))
    addInPort(Port.make<Int>("summand2"))
    addOutPort(Port.make<Int>("sum"))
  }

  override suspend fun behavior() {
    syncSchedule(inputPorts).collect { event ->
      when (event) {
        is TickEvent -> {
          outputPorts.forEach { it.pushMsg(Tick()) }
        }
        is SyncEvent -> {
          val summand1 = event[getInputPort("summand1")]?.payload as? Int ?: 0
          val summand2 = event[getInputPort("summand2")]?.payload as? Int ?: 0

          val sum = summand1 + summand2

          getOutputPort("sum").pushMsg(Message(sum))
        }
      }
    }
  }
}