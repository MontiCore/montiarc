/* (c) https://github.com/MontiCore/monticore */
package expressionStatementWellFormedness;

/**
 * Valid model.
 */
component WithAutomatonAndCorrectExpressions (int param1, int param2) {
  int myField = 0;

  automaton {
    state Start;

    initial Start / { param1 + param2 + myField; };
    Start -> Start / { param1 + param2 + myField; };
  }
}