/* (c) https://github.com/MontiCore/monticore */
package dsim.sched.atomic

import dsim.comp.ISubcomponent
import dsim.log.log
import dsim.msg.Message
import dsim.msg.Tick
import dsim.port.IDataSource
import dsim.sched.util.SingleMessageEvent
import dsim.sched.util.SyncEvent
import dsim.sched.util.TickEvent
import dsim.sched.util.nextMessage
import kotlinx.coroutines.flow.flow

/**
 * Produces a flow of SingleMessageEvents in order of selection.
 * Can react to mutations to [inputs] if a reference (NOT A COPY) is given.
 * Optionally, a collection of subcomponents can be given.
 * In that case, output ports of the subcomponents are requested each iteration
 * and scheduled together with inputs.
 */
fun untimedSchedule(inputs: Collection<IDataSource>, scs: Collection<ISubcomponent> = setOf()) = flow {
  while (true) {

    val (p, m) = nextMessage(inputs + scs.flatMap { it.outputPorts })

    when (m) {
      is Message -> {
        log("sending $p, $m to buffer")
        emit(SingleMessageEvent(p, m))
      }
    }
  }
}

/**
 * Produces a flow of SingleMessageEvents and TickEvents
 */
fun timedSchedule(inputs: Collection<IDataSource>, scs: Collection<ISubcomponent> = setOf()) = flow {

  val blocked: MutableSet<IDataSource> = mutableSetOf()

  while (true) {

    if (blocked.containsAll(inputs)) {
      log("sending Tick to buffer and resetting blocked")
      blocked.clear()
      emit(TickEvent())
    }

    val (p, m) = nextMessage((inputs + scs.flatMap { it.outputPorts }).filter { it !in blocked })

    when (m) {
      is Message -> {
        log("sending $p, $m to buffer")
        emit(SingleMessageEvent(p, m))
      }
      is Tick -> {
        log("blocking $p")
        blocked.add(p)

        if (blocked.containsAll(inputs)) {
          log("sending Tick to buffer and resetting blocked")
          blocked.clear()
          emit(TickEvent())
        }
      }
    }
  }
}

fun syncSchedule(inputs: Collection<IDataSource>, scs: Collection<ISubcomponent> = setOf()) = flow {
  val blocked: MutableSet<IDataSource> = mutableSetOf()

  val bufferedSyncEvent: MutableMap<IDataSource, Message> = mutableMapOf()

  while (true) {

    if (blocked.containsAll(inputs)) {
      log("sending sync event")
      blocked.clear()
      emit(SyncEvent(bufferedSyncEvent))
      bufferedSyncEvent.clear()
      emit(TickEvent())
    }

    val (p, m) = nextMessage((inputs + scs.flatMap { it.outputPorts }).filter { it !in blocked })

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
  }
}