/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInStatements;

// valid
component ValidIncrement(boolean parameter) {
  port in double inPort;
  port out double outPort;

  int variable = 42;

  automaton {
    state Begin;
    state End;
    initial Begin / { outPort = 0; };

    Begin -> End / {++variable; outPort = 0;};
  }
}