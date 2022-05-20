/* (c) https://github.com/MontiCore/monticore */
package dsim.reactions

import dsim.port.IDataSink
import dsim.port.IDataSource
import openmodeautomata.runtime.DataType

class TypeInReaction(val type: Class<*>) : DataType {
  override fun equalsOrExtends(other: DataType) = other.equalsOrGeneralizes(this)
  override fun equalsOrGeneralizes(other: DataType) = type.isAssignableFrom((other as TypeInReaction).type)
  override fun getName(): String = type.name
}

fun <T> Boolean.thenTake(t: T): T? = if (this) t else null
fun Any.toSink(): IDataSink? = if (this is IDataSink) this else null
fun Any.toSource(): IDataSource? = if (this is IDataSource) this else null
val Any.portType: Class<*>
  get() = toSink()?.type
      ?: toSource()?.type!!