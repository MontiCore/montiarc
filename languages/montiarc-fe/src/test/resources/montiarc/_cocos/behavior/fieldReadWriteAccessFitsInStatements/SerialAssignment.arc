/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInStatements;

// valid
component SerialAssignment(boolean parameter) {
  port in double inPort;
  port out double outPort;

  double doubleVariable = 4.2;

  automaton {
    state Begin;
    state End;
    initial Begin / { outPort = 0; };

    Begin -> End / {outPort = doubleVariable = inPort;};
  }
}