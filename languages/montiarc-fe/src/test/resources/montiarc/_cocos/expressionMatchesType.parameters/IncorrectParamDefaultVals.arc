/* (c) https://github.com/MontiCore/monticore */
package expressionMatchesType.parameters;

/**
 * Invalid model.
 */
component IncorrectParamDefaultVals(
  int anInt = 5.3,
  boolean aBool = "yolo"
) { }