/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

/**
 * valid
 * a bit more complex than EmptyStateChart
 */
component B_JustSomeStates {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  statechart Door {
    state Opened;
    initial state Closed;
    state Locked;
  }
}