/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

// valid
component UsedVariable {
  boolean variable = true;

  automaton {
    initial state Begin;
    state End;

    Begin -> End / {variable = !variable;};
  }
}