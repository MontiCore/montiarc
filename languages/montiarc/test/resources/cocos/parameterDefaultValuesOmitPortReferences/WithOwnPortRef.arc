/* (c) https://github.com/MontiCore/monticore */
package parameterDefaultValuesOmitPortReferences;

/**
 * Invalid model.
 */
component WithOwnPortRef(
  int firstParam = 2 + 4 * someInPort,
  int secondParam = 2 - 8 * someOutPort
  ) {
  port in int someInPort,
    out int someOutPort;
}
