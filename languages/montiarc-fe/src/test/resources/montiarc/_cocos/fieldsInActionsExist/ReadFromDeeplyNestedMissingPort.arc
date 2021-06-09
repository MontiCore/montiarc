/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

// invalid, unlock cannot be readed because it is missing
component ReadFromDeeplyNestedMissingPort {
  port out boolean ringing;

  automaton {
    initial state Closed;
    state Opened;

    Locked -> Closed / {ringing = 5 + 3 <= 4 && !(unlock != 4 > 5);};
  }

}