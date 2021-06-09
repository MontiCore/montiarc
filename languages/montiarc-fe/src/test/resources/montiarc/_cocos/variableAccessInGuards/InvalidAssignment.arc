/* (c) https://github.com/MontiCore/monticore */
package variableAccessInGuards;

// invalid, cannot write to inPort
component InvalidAssignment(boolean parameter) {
  port in double inPort;
  port out double outPort;

  int variable = 42;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [inPort = 5.7 == 5.7];
  }
}