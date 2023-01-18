/* (c) https://github.com/MontiCore/monticore */
package fieldInitExpressionsOmitPortReferences;

/**
 * Valid model.
 */
component WithoutPortRef(int someParam) {
  port in int someInPort,
    out int someOutPort;

  int someField = 2 + 8 * someParam;

  Inner inner(5);
  component Inner(int someInnerParam) {
    port in int someInnerInPort,
    out int someInnerOutPort;

    int someInnerField = 2 + 5 * someInnerParam;
  }

  IndependentComp independentComp;
  IndependentChild independentInstance;
}
