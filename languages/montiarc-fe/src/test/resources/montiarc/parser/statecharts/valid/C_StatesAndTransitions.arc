/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

/**
 * valid
 * a bit more complex than JustSomeStates
 */
component C_StatesAndTransitions {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton {
    initial state Closed;
    state Locked;
    state Opened;

    Opened -> Locked;
    Locked -> Closed;
    Closed -> Opened;

  }
}