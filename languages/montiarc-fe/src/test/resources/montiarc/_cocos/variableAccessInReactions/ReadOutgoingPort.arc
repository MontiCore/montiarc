/* (c) https://github.com/MontiCore/monticore */
package variableAccessInReactions;

// invalid, because the value of outPort is unknown
component ReadOutgoingPort(boolean parameter) {
  port in double inPort;
  port out double outPort;

  boolean boolVariable = true;

  automaton {
    initial state Begin;
    state End;

    Begin -> End / {boolVariable = outPort < 5;};
  }
}