/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

component EmptyReaction {

  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton {
    state Open;
    initial state Closed;
    state Locked;

    // invalid: you can omit the brackets if the reaction is empty
    Open -> Closed / {};
  }
}
