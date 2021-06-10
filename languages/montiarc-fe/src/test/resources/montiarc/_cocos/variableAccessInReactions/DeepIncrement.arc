/* (c) https://github.com/MontiCore/monticore */
package variableAccessInReactions;

// invalid, because ++ implies a change to the variable
component DeepIncrement(boolean parameter) {
  port in int inPort;
  port out int outPort;

  boolean boolVariable = true;

  automaton {
    initial state Begin;
    state End;

    Begin -> End / {boolVariable = 6 + (inPort++ -1-2-3) / 6 == 7 * 6;};
  }
}