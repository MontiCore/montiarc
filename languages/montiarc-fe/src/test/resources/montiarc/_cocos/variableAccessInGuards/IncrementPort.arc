/* (c) https://github.com/MontiCore/monticore */
package variableAccessInGuards;

// invalid, because ++ implies a change to the variable
component IncrementPort(boolean parameter) {
  port in int inPort;
  port out int outPort;

  double variable = 0.001;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [inPort++ == 42];
  }
}