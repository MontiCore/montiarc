/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

// invalid because the variable "unknown" (which appears in only one of the actions) is not defined
component OnlyOneMissing {
  boolean variable = true;

  automaton {
    initial state Begin;
    state End {
      entry / {variable = !variable;}
      exit / {unknown = variable;}
    };

    Begin -> End / {variable = false;};
  }
}