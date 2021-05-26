/* (c) https://github.com/MontiCore/monticore */
package fieldsInGuardsExist;

// invalid because the variable "unknown" is not defined
component MissingVariable {
  boolean variable = true;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [unknown == true];
  }
}