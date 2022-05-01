/* (c) https://github.com/MontiCore/monticore */
package dsim.port

import dsim.msg.IMessage
import kotlinx.coroutines.channels.SendChannel

/**
 * Port interface responsible for Output.
 * Components address their own Out-Ports via this interface.
 */
interface IDataSink {
  suspend fun pushMsg(msg: IMessage)

  val name: String

  val sendChannel: SendChannel<IMessage>

  fun close()
  val closed: Boolean

  val type: Class<*>
}
