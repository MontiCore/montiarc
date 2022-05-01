/* (c) https://github.com/MontiCore/monticore */
package fieldInitExpressionsOmitPortReferences;

/**
 * Invalid model.
 */
component WithOwnPortRef {
  port in int someInPort,
    out int someOutPort;

  int firstField = 2 + 4 * someInPort;
  int secondField = 2 - 8 * someOutPort;
}