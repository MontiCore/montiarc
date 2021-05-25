/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

/**
 * valid
 * a bit more complex than TransitionsWithReactions
 */
component F_HierarchicalStates {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  statechart Door {
    initial state Closed;
    state Locked;
    state Opened { };

    Opened -> Closed;
    Closed -> Opened [open] / {ringing = true;};
    Closed -> Locked        / {System.out.println("Door locked now.");};
    Locked -> Closed [unlock == true];

  }
}