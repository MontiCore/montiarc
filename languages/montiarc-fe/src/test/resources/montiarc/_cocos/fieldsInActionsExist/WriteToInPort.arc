/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

// invalid, unlock cannot be assigned
component WriteToInPort {
  port in boolean unlock;
  port out boolean ringing;

  automaton {
    initial state Closed;
    state Opened;

    Locked -> Closed / {unlock = false;};
  }

}