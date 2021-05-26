/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

component EmptyGuard {

  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton {
    state Open;
    initial state Closed;
    state Locked;

    // invalid: you can omit the brackets if the guard is empty
    Open -> Closed [];
  }
}