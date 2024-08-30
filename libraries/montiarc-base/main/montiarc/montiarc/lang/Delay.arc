/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

/**
 * This component delays messages by one unit of time. It does not change the
 * messages' contents. Messages are emitted in the order they are received.
 * The component does not omit any message and does not create new messages.
 */
component Delay<T> {

  port in T i;
  port <<delayed>> out T o;

  automaton {
    initial state S;
    S -> S i / {
      o = i;
    };
  }

}
