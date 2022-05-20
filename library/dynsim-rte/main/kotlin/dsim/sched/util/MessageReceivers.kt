/* (c) https://github.com/MontiCore/monticore */
package dsim.sched.util

import dsim.log.log
import dsim.msg.IMessage
import dsim.msg.Message
import dsim.port.IDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select

/**
 * Selects the next available message from a collection of data sources.
 * Will not return, if the given collection is empty.
 */
suspend fun nextMessageOrLock(inputs: Collection<IDataSource>) = select<Pair<IDataSource, IMessage>> {
  // select next available message from inputs
  log("selecting next message from $inputs")
  inputs.forEach { input ->
    input.receiveChannel.onReceive { msg ->
      log("selected $msg from $input")
      Pair(input, msg)
    }
  }
}

/**
 * Receives and returns a message or -if the input-collection is empty- null.
 * In the latter case the method sleeps some time ([TICK_INTERVAL_MS]) before returning
 */
suspend fun nextMessageOrNull(inputs: Collection<IDataSource>): Pair<IDataSource, IMessage>? {
  return if(inputs.isEmpty()){
    delay(TICK_INTERVAL_MS)
    null
  } else {
    nextMessageOrLock(inputs)
  }
}

/**
 * Does the same as [nextMessageOrNull] but returns a dummy message where the other method would return null
 */
suspend fun nextMessageOrDummy(inputs: Collection<IDataSource>): Pair<IDataSource, IMessage> {
  return nextMessageOrNull(inputs)?:Pair(shamPort, Message(Unit))
}

/**
 * a port-mockup which can be returned if there is no message to be expected and the port won't be used
 */
private val shamPort = object: IDataSource{
  override suspend fun pullMsg() = throw RuntimeException("Fake Port may not be used")
  override val name = "Fake Port"
  override val receiveChannel
    get() = throw RuntimeException("Fake Port may not be used")
  override fun close() {}
  override val closed = true
  override val type
    get() = Unit.javaClass
}

/**
 * The delay between two ticks outputted by a component without input ports (=without external schedule reference).
 * Does not include computation time.
 */
var TICK_INTERVAL_MS = 100L

/**
 * An alternative for scheduling components without input ports is adding a hidden input.
 * The name of those ports does not matter, but it should always be the same.
 */
const val CONTROL_PORT = "c.p."