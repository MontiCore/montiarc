/* (c) https://github.com/MontiCore/monticore */
package fieldInitExpressionsOmitPortReferences;

/**
 * Invalid model.
 */
component WithSubCompPortRef {
  component Inner inner {
    port in int someInPort,
      out int someOutPort;
  }

  int firstField = 2 + 4 * inner.someInPort;
  int secondField = 2 - 8 * inner.someOutPort;
}