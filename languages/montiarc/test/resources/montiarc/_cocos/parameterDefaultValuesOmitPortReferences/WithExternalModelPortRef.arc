/* (c) https://github.com/MontiCore/monticore */
package parameterDefaultValuesOmitPortReferences;

/**
 * Invalid model.
 */
component WithExternalModelPortRef(
  int firstParam = 5 + 2 * innerInstance.someIndependentInPort,
  int secondParam = 4 + 3 * innerInstance.someIndependentOutPort
  ) {
  IndependentComp innerInstance;
}