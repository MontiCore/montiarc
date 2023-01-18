/* (c) https://github.com/MontiCore/monticore */
package expressionStatementWellFormedness;

/**
 * Valid model.
 */
component WithComputeBlockAndCorrectExpressions (int param1, int param2) {
  int myField = param1 + param2;

  compute {
    myField++;
    String a = "foo";
  }
}
