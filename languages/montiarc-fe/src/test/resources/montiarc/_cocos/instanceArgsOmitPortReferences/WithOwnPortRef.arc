/* (c) https://github.com/MontiCore/monticore */
package instanceArgsOmitPortReferences;

import instanceArgsOmitPortReferences.toInstantiate.*;

/**
 * Invalid model.
 */
component WithOwnPortRef {
  port in int someInPort,
    out int someOutPort;

  WithParams withParams(
    24 / 2,
    2 + 4 * someInPort,    // illegal port reference
    2 - 8 * someOutPort    // illegal port reference
  );
}