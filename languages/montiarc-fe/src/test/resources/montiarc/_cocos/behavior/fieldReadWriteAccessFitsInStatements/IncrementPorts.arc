/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInStatements;

// invalid, because ++ requires read AND write access, ports only offer one
component IncrementPorts(boolean parameter) {
  port in int inPort;
  port out int outPort;

  int variable = 42;

  automaton {
    state Begin;
    state End;

    initial Begin / {
      variable++;
      inPort++;
      outPort++;
    };

    Begin -> End / {
      variable++;
      inPort++;
      outPort++;
    };
  }
}