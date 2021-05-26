/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

// invalid because the variable "unknown" is not defined
component MissingVariableInAction {
  boolean variable = true;

  automaton {
    initial state Begin;
    state End {
      exit / {unknown = 24;}
    };

    Begin -> End;
  }
}