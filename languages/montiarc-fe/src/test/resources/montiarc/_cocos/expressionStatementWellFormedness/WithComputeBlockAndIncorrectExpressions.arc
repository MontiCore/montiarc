/* (c) https://github.com/MontiCore/monticore */
package expressionStatementWellFormedness;

/**
 * Invalid model.
 */
component WithComputeBlockAndIncorrectExpressions (int param1 = "Hello" / 2) {
  port out int outP;
  int myField = "Hello" / 2;

  compute {
    myField = "Hello" / 2;
    int a = "Hello" / 2;
  }
}