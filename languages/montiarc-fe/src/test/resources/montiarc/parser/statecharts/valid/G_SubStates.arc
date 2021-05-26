/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

/**
 * valid
 * a bit more complex than HierarchicalStates
 */
component G_SubStates {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton {
    initial state Closed;
    state Locked;
    state Opened {
      initial state SwingOpen;
      state IsOpen;

      SwingOpen -> IsOpen;
    };

    Opened -> Closed;
    Closed -> Opened [open] / {ringing = true;};
    Closed -> Locked        / {System.out.println("Door locked now.");};
    Locked -> Closed [unlock == true];

  }
}