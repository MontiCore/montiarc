/* (c) https://github.com/MontiCore/monticore */
package parameterDefaultValuesOmitPortReferences;

/**
 * Valid model.
 */
component WithoutPortRef(int someParam = 2 * 5) {
  port in int someInPort,
    out int someOutPort;

  Inner inner(5);
  component Inner(int someInnerParam = 2 + 5) {
    port in int someInnerInPort,
    out int someInnerOutPort;
  }

  IndependentComp independentComp;
  IndependentChild independentInstance;
}
