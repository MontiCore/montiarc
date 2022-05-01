/* (c) https://github.com/MontiCore/monticore */
package instanceArgsOmitPortReferences;

import instanceArgsOmitPortReferences.toInstantiate.*;

/**
 * Valid model.
 */
component WithInheritedPortRef extends IndependentComp {
  WithParams withParams(
    24 / 2,
    5 + 2 * someIndependentInPort,    // illegal port reference
    4 + 3 * someIndependentOutPort    // illegal port reference
  );
}