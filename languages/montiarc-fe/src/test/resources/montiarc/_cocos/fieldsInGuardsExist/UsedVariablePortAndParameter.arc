/* (c) https://github.com/MontiCore/monticore */
package fieldsInGuardsExist;

// valid
component UsedVariablePortAndParameter(boolean parameter) {
  port in boolean inPort;

  boolean variable = true;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [parameter == true];
    Begin -> End [variable == true];
    Begin -> End [inPort == true];
  }
}