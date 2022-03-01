/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInStatements;

// invalid, because ++ implies a change to the variable
component DeepIncrement(boolean parameter) {
  port in int inPort;
  port out int outPort;

  boolean boolVariable = true;

  automaton {
    state Begin;
    state End;

    initial Begin / {
      boolVariable = 6 + (inPort++ -1-2-3) / 6 == 7 * 6;
      outPort = 0;
    };

    Begin -> End / {
      boolVariable = 6 + (inPort++ -1-2-3) / 6 == 7 * 6;
      outPort = 0;
    };
  }
}