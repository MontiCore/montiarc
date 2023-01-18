/* (c) https://github.com/MontiCore/monticore */
package parameterDefaultValuesOmitPortReferences;

/**
 * Invalid model.
 */
component WithSubCompPortRef(
  int firstParam = 2 + 4 * inner.someInPort,
  int firstParam = 2 - 8 * inner.someOutPort
  ) {
  component Inner inner {
    port in int someInPort,
      out int someOutPort;
  }
}
