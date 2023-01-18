/* (c) https://github.com/MontiCore/monticore */
package parameterDefaultValueTypesCorrect;

/**
 * Invalid model. For testing purposes, let the types 'String' and 'Person' be resolvable.
 */
component TypeRefAsDefaultValue(
  String anInt = String,
  double aDouble = Person
) { }
