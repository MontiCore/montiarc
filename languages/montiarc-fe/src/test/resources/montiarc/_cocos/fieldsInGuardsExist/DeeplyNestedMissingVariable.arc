/* (c) https://github.com/MontiCore/monticore */
package fieldsInGuardsExist;

// invalid, because the variable count is not defined
component DeeplyNestedMissingVariable {
  port in boolean input;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [input == !(5 <= count + 4)];
  }
}