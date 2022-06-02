/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInStatements;

// invalid, because ++ requires read AND write access, ports only offer one
component IncrementPorts(boolean parameter) {
  port in int inPort;
  port out int outPort;

  int variable = 42;

  automaton {
    initial {
      variable++;
      inPort++;
      outPort++;
    } state Begin;

    state End;

    Begin -> End / {
      variable++;
      inPort++;
      outPort++;
    };
  }
}