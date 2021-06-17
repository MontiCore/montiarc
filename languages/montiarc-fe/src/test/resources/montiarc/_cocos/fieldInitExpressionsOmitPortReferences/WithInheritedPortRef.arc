/* (c) https://github.com/MontiCore/monticore */
package fieldInitExpressionsOmitPortReferences;

/**
 * Valid model.
 */
component WithInheritedPortRef extends IndependentComp {
  int firstField = 2 + 4 * someIndependentInPort;
  int secondField = 2 - 8 * someIndependentOutPort;
}