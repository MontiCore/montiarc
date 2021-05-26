/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

// invalid, ringing cannot be readed
component ReadFromOutPort {
  port in boolean unlock;
  port out boolean ringing;

  automaton {
    initial state Closed;
    state Opened;

    Locked -> Closed / {ringing = ringing && unlock;};
  }

}