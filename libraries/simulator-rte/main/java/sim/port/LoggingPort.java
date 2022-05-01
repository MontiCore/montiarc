/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import org.slf4j.Logger;

import sim.comp.ITimedComponent;
import sim.message.Message;
import sim.message.TickedMessage;

/**
 * Port that logs incoming and outgoing messages.
 *
 * @param <T> port type
 */
public class LoggingPort<T> extends Port<T> {

  /**
   * Logger to use.
   */
  private final Logger logger;

  /**
   * Flags, if ticks should be logged or not.
   */
  private final boolean doLogTicks;

  /**
   * Creates a new {@link LoggingPort} that uses the given {@link Logger} and loggs ticks.
   *
   * @param logger logger to use
   */
  public LoggingPort(final Logger logger) {
    this(logger, true);
  }

  /**
   * Creates a new {@link LoggingPort} that uses the given {@link Logger}.
   *
   * @param logger     logger to use
   * @param doLogTicks flags, if ticks should be logged or not.
   */
  public LoggingPort(final Logger logger, final boolean doLogTicks) {
    super();
    this.logger = logger;
    this.doLogTicks = doLogTicks;
  }

  /**
   * @see sim.port.Port#send(TickedMessage)
   */
  @Override
  public void send(TickedMessage<T> message) {
    super.send(message);
    if ((message.isTick() && doLogTicks) || !message.isTick()) {
      doLog("send", message);
    }
  }

  /**
   * @see sim.port.Port#accept(TickedMessage)
   */
  @Override
  public void accept(TickedMessage<? extends T> message) {
    super.accept(message);
    if ((message.isTick() && doLogTicks) || !message.isTick()) {
      doLog("accept", message);
    }
  }

  /**
   * Logs the given message combined with the given prefix.
   *
   * @param prefix  log prefix
   * @param message message to log.
   */
  protected void doLog(String prefix, TickedMessage<?> message) {
    StringBuffer log = new StringBuffer();
    log.append(prefix);
    log.append(" ");
    if (getComponent() != null) {
      log.append(getComponent().toString());
      if (getComponent() instanceof ITimedComponent) {
        log.append("(");
        log.append(((ITimedComponent) getComponent()).getLocalTime());
        log.append(") -> ");
      }
    }
    if (message.isTick()) {
      log.append(message.toString());
    } else {
      log.append(((Message<?>) message).getData().toString());
    }
    logger.debug(log.toString());
  }
}
