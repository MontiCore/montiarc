/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInStatements;

// invalid, cannot write to inPort
component InvalidAssignment(boolean parameter) {
  port in double inPort;
  port out double outPort;

  int variable = 42;

  automaton {
    initial {
      inPort = 5.7;
      outPort = 0;
    } state Begin;
    state End;

    Begin -> End / {
      inPort = 5.7;
      outPort = 0;
    };
  }
}
