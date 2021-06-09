/* (c) https://github.com/MontiCore/monticore */
package variableAccessInGuards;

// valid
component UsedAllInGuard(boolean parameter) {
  port in double inPort;
  port out double outPort;

  int variable = 42;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [parameter == (variable * inPort <= 3)];
  }
}