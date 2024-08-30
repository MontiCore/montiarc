/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

/**
 * This component delays messages by one unit of time. It does not change the
 * messages' contents. Messages are emitted in the order they are received.
 * The component does not omit any message and, besides the initial message,
 * does not create new messages. To uphold the contract of synchronous ports,
 * the component emits a message in the first time slice. The parameter iv
 * specifies that message.
 *
 * @param iv the message to be send in the first time slice
 */
component TSDelay<T>(T iv) {

  port <<sync>> in T i;
  port <<sync, delayed>> out T o;

  init {
    o = iv;
  }

  compute {
    o = i;
  }

}
