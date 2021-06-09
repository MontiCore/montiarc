/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

// valid model
component ReadFromDeeplyNestedPort {
  port in boolean unlock;
  port out boolean ringing;

  automaton {
    initial state Closed;
    state Opened;

    Locked -> Closed / {ringing = 5 + 3 <= 4 && !(unlock != 4 > 5);};
  }

}