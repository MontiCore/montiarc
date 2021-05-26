/* (c) https://github.com/MontiCore/monticore */
package parser.statecharts.valid;

/**
 * valid
 * a bit more complex than Actions
 */
component I_SelfTransitions {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton {
    initial state Closed;
    state Locked;
    state Opened {
      -> [unlock == false] / {ringing = true;};

      initial state SwingOpen;
      state IsOpen;

      SwingOpen -> IsOpen;

      entry / {System.out.println("door opens");}
      exit / {System.out.println("door closes");}
    };

    Opened -> Closed;
    Closed -> Opened [open] / {ringing = true;};
    Closed -> Locked        / {System.out.println("Door locked now.");};
    Locked -> Closed [unlock == true];

  }
}