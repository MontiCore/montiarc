/* (c) https://github.com/MontiCore/monticore */
package fieldsInGuardsExist;

// invalid because the variable "unknown" is not defined
component OnlyOneMissing {
  boolean variable = true;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [variable == true];
    Begin -> End [unknown == true];
    Begin -> End [true == true];
  }
}