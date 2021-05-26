/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

  // valid model
  // has no actions or reactions, so they can't be wrong
component NoActions {
  port in boolean open,
       in boolean unlock;
  port out boolean ringing;

  automaton {
    initial state Closed;
    state Locked;
    state Opened {
      initial state SwingOpen;
      state IsOpen;

      SwingOpen -> IsOpen [open];
    };

    Opened -> Closed;
    Closed -> Opened [unlock == open];
    Closed -> Locked;
    Locked -> Closed;
  }

}