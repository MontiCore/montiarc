/* (c) https://github.com/MontiCore/monticore */
package dsim.sched.util

import dsim.msg.IMessage
import dsim.msg.Message
import dsim.port.IDataSource

interface IEvent

data class SingleMessageEvent(val port: IDataSource, val msg: Message) : IEvent {
  operator fun get(port: IDataSource?): Message? {
    return when (port?.let { isFor(it) }){
      true -> msg
      false, null -> null
    }
  }
  fun isFor(port: IDataSource) = port == this.port
}

data class SyncEvent(val events: Map<IDataSource, Message>) : IEvent {
  operator fun get(port: IDataSource?) = events[port]
  fun isFor(port: IDataSource) = port in events.keys
}

class TickEvent : IEvent

class ForwardEvent(val port: IDataSource, val msg: IMessage) : IEvent

class WrongEventTypeScheduledException : Exception()
