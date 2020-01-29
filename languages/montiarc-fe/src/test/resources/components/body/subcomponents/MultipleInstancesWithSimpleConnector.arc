/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;
import components.body.subcomponents._subcomponents.HasTwoStringInAndOneStringOut;

/*
 * Invalid model.
 * Multiple Instances of the HasStringInputAndOutputComponent with the simple
 * connector should result in an error, as c.sIn1 would have two incoming connectors.
 *
 * @implements [Hab16] R1: Each outgoing port of a component type definition
 *   is used at most once as target of a connector. (p. 63, Lst. 3.36)
 * @implements [Hab16] R2: Each incoming port of a subcomponent is used at
 *   most once as target of a connector. (p. 62, Lst. 3.37)
 */
component MultipleInstancesWithSimpleConnector{

  component HasStringInputAndOutput a,b [pOut -> c.sIn1];

  component HasTwoStringInAndOneStringOut c;
}