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

operator fun IDataSource.rangeTo(target: IDataSink):Connector {
  if (!target.type.isAssignableFrom(this.type)) throw IncompatiblePortsException()
  return Connector(this, target)
}

class IncompatiblePortsException : Exception()
