/* (c) https://github.com/MontiCore/monticore */
package fieldInitExpressionsOmitPortReferences;

/**
 * Invalid model.
 */
component WithExternalModelInheritedPortRef {
  IndependentChild innerInstance;

  int firstField = 5 + 2 * innerInstance.someIndependentInPort;
  int secondField = 4 + 3 * innerInstance.someIndependentOutPort;
}
