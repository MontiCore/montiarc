/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

/**
 * valid
 * a bit more complex than StatesAndTransitions
 */
component D_GuardedTransitions {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  statechart Door {
    initial state Closed;
    state Locked;
    state Opened;

    Opened -> Closed;
    Closed -> Opened [open];
    Closed -> Locked;
    Locked -> Closed [unlock == true];

  }
}