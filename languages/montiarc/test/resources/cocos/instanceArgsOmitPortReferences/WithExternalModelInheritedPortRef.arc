/* (c) https://github.com/MontiCore/monticore */
package instanceArgsOmitPortReferences;

import instanceArgsOmitPortReferences.toInstantiate.*;

/**
 * Invalid model.
 */
component WithExternalModelInheritedPortRef {
  IndependentChild innerInstance;

  WithParams withParams(
    24 / 2,
    5 + 2 * innerInstance.someIndependentInPort,    // illegal port reference
    4 + 3 * innerInstance.someIndependentOutPort    // illegal port reference
  );
}
