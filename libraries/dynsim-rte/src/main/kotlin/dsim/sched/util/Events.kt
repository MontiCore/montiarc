/* (c) https://github.com/MontiCore/monticore */
package dsim.sched.util

import dsim.msg.IMessage
import dsim.msg.Message
import dsim.port.IDataSource

interface IEvent

data class SingleMessageEvent(val port: IDataSource, val msg: Message) : IEvent
data class SyncEvent(val events: Map<IDataSource, Message>) : IEvent {
  operator fun get(port: IDataSource?) = events[port]
}

class TickEvent : IEvent

class ForwardEvent(val port: IDataSource, val msg: IMessage) : IEvent

class WrongEventTypeScheduledException : Exception()
