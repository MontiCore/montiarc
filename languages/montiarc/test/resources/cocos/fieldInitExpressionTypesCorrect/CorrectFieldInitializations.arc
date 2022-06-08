/* (c) https://github.com/MontiCore/monticore */
package fieldInitExpressionTypesCorrect;

/**
 * Valid model.
 */
component CorrectFieldInitializations {
  int anInt = 32;
  double aDouble = 2.3 + 84;
  double aSecondDouble = 4 / 2;
  String str = "yea";
  boolean aBool = (4 == 3) && true;
}