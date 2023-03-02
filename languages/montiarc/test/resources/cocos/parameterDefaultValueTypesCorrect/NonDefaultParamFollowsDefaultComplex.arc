/* (c) https://github.com/MontiCore/monticore */
package parameterDefaultValueTypesCorrect;

/**
 * Invalid model.
 */
component NonDefaultParamFollowsDefaultComplex (
  int anInt = 32,
  double aDouble,
  double aSecondDouble = 4 / 2,
  String str,
  boolean aBool = (4 == 3) && true
) { }
