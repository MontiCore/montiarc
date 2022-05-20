/* (c) https://github.com/MontiCore/monticore */
package dsim.port.util

import dsim.port.IDataSink
import dsim.port.IDataSource

/**
 * Replacement for Pair collection type. More safe, easier to use.
 */
data class Connector(val base: IDataSource, val target: IDataSink)

/**
 * Returns a connector from [base] to [target] if they are of the same type
 */
fun connector(base: IDataSource, target: IDataSink): Connector {
  return base..target
}

operator fun IDataSource.rangeTo(target: IDataSink): Connector {
  if (!target.type.isAssignableFrom(this.type)) throw IncompatiblePortsException(this, target)
  return Connector(this, target)
}

class IncompatiblePortsException(val source: IDataSource, val sink: IDataSink) : Exception() {
  override val message: String?
    get() = "Cannot connect ${source.name} to ${sink.name}:\n" +
        "The latter one has the type ${sink.type.canonicalName},\n" +
        "which is not assignable from ${source.type.canonicalName}"
}
