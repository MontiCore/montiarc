/* (c) https://github.com/MontiCore/monticore */
@file:JvmName("PortUtils")

package dsim.port

import dsim.log.log
import dsim.msg.IMessage
import dsim.msg.Message
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED

open class Port(final override val name: String, override val type: Class<*>) : IPort {

  init {
    log("port created")
  }

  override fun toString(): String = "$name(${type.simpleName})"

  override var closed: Boolean = false

  companion object Factory {
    inline fun <reified T> make(name: String) = Port(name, T::class.java)
  }

  /**
   * Rendezvous implementation ensures that sending component coroutine
   * suspends as soon as it sends the message. The receiving component will
   * always receive the message, this is made sure of in the abstract component
   * implementations
   */
  private var channel: Channel<IMessage> = Channel(UNLIMITED)

  override val sendChannel get() = channel

  override val receiveChannel get() = channel

  /**
   * pushes a message to the channel and suspends as long as no one takes it.
   * Whether a connector starts at this port or not does not matter.
   * For input ports, the owning component is obligated to take it.
   * For output ports, the supercomponent is obligated to take it.
   */
  override suspend fun pushMsg(msg: IMessage) {
    if (msg is Message) {
      if (!type.isAssignableFrom(msg.payload.javaClass)) throw IncompatibleMessageTypeException()
    }

    log("message $msg pushed")
    channel.send(msg)
  }

  /**
   * Calls receive function on internal channel
   */
  override suspend fun pullMsg(): IMessage {
    val m = channel.receive()
    log("message $m pulled")
    return m
  }

  /**
   * When removing a port, it's channel should be closed.
   * This way, all eventual schedulers run out and complete.
   */
  override fun close() {
    channel.close()
    closed = true
  }
}

class IncompatibleMessageTypeException : Exception()
