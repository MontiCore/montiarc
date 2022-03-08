/* (c) https://github.com/MontiCore/monticore */
package expressionStatementWellFormedness;

/**
 * Invalid model.
 */
component WithAutomatonAndIncorrectExpressions (int param1) {
  int myField = 0;

  automaton {
    state Start;

    initial Start / { "Hello" / 2; };
    Start -> Start / {
      int a = "Hello" / 2;
    };
  }
}