/* (c) https://github.com/MontiCore/monticore */
package instanceArgsOmitPortReferences;

import instanceArgsOmitPortReferences.toInstantiate.*;

/**
 * Valid model.
 */
component WithoutPortRef(int someParam) {
  port in int someInPort,
    out int someOutPort;

  int someField = 2 + 8 * someParam;

  WithParams withParams(24 / 2, 2 * someField, 3 * someParam);

  Inner inner(5);
  component Inner(int someInnerParam) {
    port in int someInnerInPort,
    out int someInnerOutPort;

    int someInnerField = 2 + 5 * someInnerParam;

    WithParams innerWithParams(24 / 2, 2 * someInnerField, 3 * someInnerParam);
  }

  IndependentComp independentComp;
  IndependentChild independentInstance;
}