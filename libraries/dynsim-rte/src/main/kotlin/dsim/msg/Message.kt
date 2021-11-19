/* (c) https://github.com/MontiCore/monticore */
package dsim.msg

/**
 * Generic Message class.
 * Takes a payload of type T.
 */
interface IMessage {
  val payload: Any?
}

class Message(override val payload: Any) : IMessage {
  override fun toString(): String {
    return payload.toString()
  }
}

class Tick : IMessage {
  override val payload: Nothing? = null
  override fun toString(): String {
    return "(TICK)"
  }
}
