/* (c) https://github.com/MontiCore/monticore */
package variableAccessInGuards;

// valid
component ValidIncrement(boolean parameter) {
  port in double inPort;
  port out double outPort;

  int variable = 42;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [++variable > 100];
  }
}