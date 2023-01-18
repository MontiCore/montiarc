/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

import components.body.subcomponents._subcomponents.ExtendsHasStringInputAndOutput;
import components.body.subcomponents._subcomponents.ExtendsExtendsHasStringInputAndOutput;
import components.body.subcomponents._subcomponents.HasIntegerInputAndOutput;

/*
 * Invalid model.
 *
 * @implements [Hab16] R8: R8: The target port in a connection has to be compatible to the source port, i.e.,
 * the type of the target port is identical or a supertype of the source port type. (p.66, lst. 3.43)
 */
component ConnectsIncompatibleInheritedPorts {

  component ExtendsHasStringInputAndOutput sub1;
  component ExtendsExtendsHasStringInputAndOutput sub3;
  component HasIntegerInputAndOutput sub2;

  connect sub1.pOut -> sub2.int1;
    // Error: Ports are not compatible. sub1.pOut is of type String and sub2.int1
    // is of type Integer
  connect sub2.int2 -> sub1.pIn;
    // Error: Ports are not compatible. sub2.int2 is of type Integer and sub1.pIn
    // is of type String
  connect sub1.pOut -> sub2.notexisting; // Error

  connect sub2.int2 -> sub3.pIn;
    // Error: Ports are not compatible. sub2.int2 is of type Integer and sub1.pIn
    // is of type String

}
