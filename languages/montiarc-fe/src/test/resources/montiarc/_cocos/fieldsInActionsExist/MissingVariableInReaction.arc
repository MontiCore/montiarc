/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

// invalid because the variable "unknown" is not defined
component MissingVariableInReaction {
  boolean variable = true;

  automaton {
    initial state Begin;
    state End;

    Begin -> End / {unknown = 24;};
  }
}