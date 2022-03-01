/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInStatements;

// invalid, because the value of outPort is unknown
component ReadOutgoingPort(boolean parameter) {
  port in double inPort;
  port out double outPort;

  boolean boolVariable = true;

  automaton {
    state Begin;
    state End;
    initial Begin / {
      boolVariable = outPort < 5;
      outPort = 0;
    };

    Begin -> End / {
      boolVariable = outPort < 5;
      outPort = 0;
    };
  }
}