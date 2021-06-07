/* (c) https://github.com/MontiCore/monticore */
package fieldsInGuardsExist;

// valid model
component DeeplyNestedVariable {
  port in boolean input;
  port in int count;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [input == !(5 <= count + 4)];
  }
}