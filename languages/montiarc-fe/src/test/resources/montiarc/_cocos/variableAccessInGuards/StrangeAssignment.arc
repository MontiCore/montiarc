/* (c) https://github.com/MontiCore/monticore */
package variableAccessInGuards;

// valid, because outPort is not actually read, the assignment is read and returns 5.7
component StrangeAssignment(boolean parameter) {
  port in double inPort;
  port out double outPort;

  int variable = 42;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [outPort = 5.7 == 5.7];
  }
}