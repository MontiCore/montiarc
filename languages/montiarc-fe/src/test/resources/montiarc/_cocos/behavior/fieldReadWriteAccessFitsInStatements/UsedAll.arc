/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInStatements;

// valid
component UsedAll(boolean parameter) {
  port in double inPort;
  port out double outPort;

  int variable = 42;

  automaton {
    state Begin;
    state End;
    initial Begin / { outPort = 0; };

    Begin -> End / {
      outPort = inPort + variable;
      variable += parameter ? -1 : 1;
    };
  }
}