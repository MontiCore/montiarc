/* (c) https://github.com/MontiCore/monticore */
package fieldsInGuardsExist;

// valid
component UsedVariable {
  boolean variable = true;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [variable];
  }
}