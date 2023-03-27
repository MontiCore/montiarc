/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetTypesFit.subcomponentDefinitions;

import java.util.List;

/**
 * Valid model.
 */
component Generic<A> {
  port in A aIn;
  port out A aOut;
  port in List<A> aListIn;
  port out List<A> aListOut;
}
