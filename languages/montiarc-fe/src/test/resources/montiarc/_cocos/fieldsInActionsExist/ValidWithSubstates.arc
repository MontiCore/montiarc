/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

  // valid model
component ValidWithSubstates(StringBuilder log) {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton {
    initial state Closed;
    state Locked;
    state Opened {
      initial state SwingOpen;
      state IsOpen;

      SwingOpen -> IsOpen [open] / {ringing = !unlock;};
    };

    Opened -> Closed;
    Closed -> Opened [open] / {ringing = true;};
    Closed -> Locked        / {log.append("Door locked now.");};
    Locked -> Closed [unlock == true];
  }
}