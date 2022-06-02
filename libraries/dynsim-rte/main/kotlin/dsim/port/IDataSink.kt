/* (c) https://github.com/MontiCore/monticore */
package dsim.port

import dsim.msg.IMessage
import kotlinx.coroutines.channels.SendChannel
import openmodeautomata.runtime.SourcePort
import openmodeautomata.runtime.TargetPort
import openmodeautomata.runtime.UndirectedPort

/**
 * Port interface responsible for Output.
 * Components address their own Out-Ports via this interface.
 */
interface IDataSink : TargetPort {
  suspend fun pushMsg(msg: IMessage)

  val name: String

  val sendChannel: SendChannel<IMessage>

  fun close()
  val closed: Boolean

  val type: Class<*>

  override fun equalsType(other: UndirectedPort): Boolean {
    return ((other as? IDataSink)?.type ?: ((other as? IDataSource)?.type))?.equals(type)?:false
  }

  override fun matchesType(other: SourcePort): Boolean {
    return type.isAssignableFrom((other as IDataSource).type)
  }
}
