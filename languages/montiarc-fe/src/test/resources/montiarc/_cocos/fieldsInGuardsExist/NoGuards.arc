/* (c) https://github.com/MontiCore/monticore */
package fieldsInGuardsExist;

  // valid model
  // has no guards, so they can't be wrong
component NoGuards {
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
    Closed -> Opened / {ringing = true;};
    Closed -> Locked / {System.out.println("Door locked now.");};
    Locked -> Closed;
  }

}