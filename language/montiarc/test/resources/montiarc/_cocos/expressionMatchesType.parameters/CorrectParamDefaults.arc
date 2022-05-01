/* (c) https://github.com/MontiCore/monticore */
package expressionMatchesType.parameters;

/**
 * Valid model.
 */
component CorrectParamDefaults(
  int anInt = 32,
  double aDouble = 2.3 + 84,
  double aSecondDouble = 4 / 2,
  String str = "yea",
  boolean aBool = (4 == 3) && true
) { }