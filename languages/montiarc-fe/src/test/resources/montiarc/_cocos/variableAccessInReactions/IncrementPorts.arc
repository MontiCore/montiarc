/* (c) https://github.com/MontiCore/monticore */
package variableAccessInReactions;

// invalid, because ++ requires read AND write access, ports only offer one
component IncrementPorts(boolean parameter) {
  port in int inPort;
  port out int outPort;

  int variable = 42;

  automaton {
    initial state Begin;
    state End;

    Begin -> End / {
      variable++;
      inPort++;
      outPort++;
    };
  }
}