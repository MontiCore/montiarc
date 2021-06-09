/* (c) https://github.com/MontiCore/monticore */
package variableAccessInGuards;

// invalid, because ++ implies a change to the variable
component DeepIncrement(boolean parameter) {
  port in int inPort;
  port out int outPort;

  double variable = 0.001;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [6 + (inPort++ -1-2-3) / 6 == 7 * 6];
  }
}