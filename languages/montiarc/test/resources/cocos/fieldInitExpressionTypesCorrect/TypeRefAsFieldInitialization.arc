/* (c) https://github.com/MontiCore/monticore */
package fieldInitExpressionTypesCorrect;

/**
 * Invalid model. For testing purposes, let the types 'String' and 'Person' be resolvable.
 */
component TypeRefAsFieldInitialization {
  int anInt = 32;
  double aDouble = Person;            // Illegal
  double aSecondDouble = 4 / 2;
  String str = String;                // Illegal
  boolean aBool = (4 == 3) && true;
}