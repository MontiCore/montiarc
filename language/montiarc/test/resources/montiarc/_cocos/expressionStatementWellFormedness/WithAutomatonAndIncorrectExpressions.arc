/* (c) https://github.com/MontiCore/monticore */
package expressionStatementWellFormedness;

/**
 * Invalid model.
 */
component WithAutomatonAndIncorrectExpressions (int param1) {
  int myField = 0;

  automaton {
    initial { "Hello" / 2; } state Start;
    Start -> Start / {
      int a = "Hello" / 2;
    };
  }
}