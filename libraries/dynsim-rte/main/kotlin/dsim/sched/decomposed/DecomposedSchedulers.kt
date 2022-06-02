/* (c) https://github.com/MontiCore/monticore */
package dsim.sched.decomposed

import dsim.comp.ISubcomponent
import dsim.log.log
import dsim.msg.Message
import dsim.msg.Tick
import dsim.port.IDataSource
import dsim.sched.util.*
import kotlinx.coroutines.flow.flow

fun untimedSchedule(inputs: Collection<IDataSource>, scs: Collection<ISubcomponent> = setOf()) = flow {
  while (true) {

    val (p, m) = nextMessageOrDummy(inputs + scs.flatMap { it.outputPorts })

    when (m) {
      is Message -> {
        log("sending $p, $m to buffer")
        emit(SingleMessageEvent(p, m))
      }
    }

    emit(ForwardEvent(p, m))
  }
}

fun timedSchedule(inputs: Collection<IDataSource>, scs: Collection<ISubcomponent> = setOf()) = flow {

  val blocked: MutableSet<IDataSource> = mutableSetOf()

  while (true) {
    // Take snapshot copy of all inputs
    val inputsSnapshot = inputs + scs.flatMap { it.outputPorts }

    // If all are blocked, emit tick event
    if (blocked.containsAll(inputsSnapshot)) {
      log("sending Tick to buffer and resetting blocked")
      blocked.clear()
      emit(TickEvent())
    }
    // otherwise, schedule next message
    else {
      val (p, m) = nextMessageOrDummy((inputsSnapshot).filter { it !in blocked })

      when (m) {
        is Message -> {
          log("sending $p, $m to buffer")
          emit(SingleMessageEvent(p, m))
        }
        is Tick -> {
          log("blocking $p")
          blocked.add(p)
        }
      }

      emit(ForwardEvent(p, m))
    }
  }
}

fun syncSchedule(inputs: Collection<IDataSource>, scs: Collection<ISubcomponent> = setOf()) = flow {
  println("Sync schedule of component with subcomponents${scs.map { r->r.name+", " }}")

  val blocked: MutableSet<IDataSource> = mutableSetOf()

  val bufferedSyncEvent: MutableMap<IDataSource, Message> = mutableMapOf()

  while (true) {
    // Take snapshot copy of all inputs
    val inputsSnapshot = inputs + scs.flatMap { it.outputPorts }

    // If all are blocked, emit tick event
    if (blocked.containsAll(inputsSnapshot)) {
      log("sending sync event")
      blocked.clear()
      emit(SyncEvent(bufferedSyncEvent))
      bufferedSyncEvent.clear()
      emit(TickEvent())
    }
    // Otherwise, schedule next message
    else {
      val (p, m) = nextMessageOrNull(inputsSnapshot.filter { it !in blocked })?:continue

      when (m) {
        is Message -> {
          log("adding $p, $m to sync event")
          bufferedSyncEvent[p] = m
        }
        is Tick -> {
          log("blocking $p")
          blocked.add(p)
        }
      }
      emit(ForwardEvent(p, m))
    }
  }
}
