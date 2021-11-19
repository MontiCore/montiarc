/* (c) https://github.com/MontiCore/monticore */
package dsim.port

import dsim.msg.IMessage
import kotlinx.coroutines.channels.ReceiveChannel

/**
 * Port Interface responsible for Input.
 * Components should address their own In-Ports via this interface.
 */
interface IDataSource {
  suspend fun pullMsg(): IMessage

  val name: String

  val receiveChannel: ReceiveChannel<IMessage>

  fun close()
  val closed: Boolean

  val type: Class<*>
}
