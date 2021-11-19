/* (c) https://github.com/MontiCore/monticore */
package dsim.sched.util

import dsim.log.log
import dsim.msg.IMessage
import dsim.port.IDataSource
import kotlinx.coroutines.selects.select

/**
 * Selects the next available message from a collection of data sources.
 */
suspend fun nextMessage(inputs: Collection<IDataSource>) = select<Pair<IDataSource, IMessage>> {
  // select next available message from inputs
  log("selecting next message from $inputs")
  inputs.forEach { input ->
    input.receiveChannel.onReceive { msg ->
      log("selected $msg from $input")
      Pair(input, msg)
    }
  }
}
