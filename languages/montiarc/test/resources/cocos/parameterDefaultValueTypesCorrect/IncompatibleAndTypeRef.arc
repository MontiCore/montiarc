/* (c) https://github.com/MontiCore/monticore */
package parameterDefaultValueTypesCorrect;

/**
 * Invalid model. For testing purposes, let the types 'String' and 'Person' be resolvable.
 */
component IncompatibleAndTypeRef(
  String aString = String,
  int anInt = 5.3,
  double aDouble = Person,
  boolean aBool = "yolo"
) { }
