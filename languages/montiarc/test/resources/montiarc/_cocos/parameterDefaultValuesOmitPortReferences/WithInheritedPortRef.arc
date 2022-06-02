/* (c) https://github.com/MontiCore/monticore */
package parameterDefaultValuesOmitPortReferences;

/**
 * Valid model.
 */
component WithInheritedPortRef(
  int firstParam = 2 + 4 * someIndependentInPort,
  int secondParam = 2 - 8 * someIndependentOutPort
  ) extends IndependentComp { }