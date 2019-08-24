/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

import java.lang.String;
import java.lang.Integer;

/*
 * Invalid model.
 * Fully qualified port names in connectors do not exist in the component.
 *
 *  Formerly named "R3" in MontiArc3.
 *
 * @implements [Hab16] R5: The first part of a qualified connector’s source
                              respectively target must correspond to a sub-
                              component declared in the current component
                              definition. (p.64 Lst. 3.40)
 * @implements [Hab16] R6: The second part of a qualified connector’s source
                              respectively target must correspond to a port
                              name of the referenced subcomponent determined
                              by the first part. (p.64 Lst. 3.41)
 */
component ConnectorReferenceDoesNotExist {

  port 
    in String str1,
    in String str2,
    out Integer int1,
    out Integer int2;
  
  component components.body.subcomponents._subcomponents.HasStringInputAndIntegerOutput p;
  
  connect str1 -> p.portIn;
  connect p.portOut -> int1;
  
  connect str2 -> undefRef1.portIn;
    //ERROR: The reference 'undefRef1' does not exist!
  connect RefType.portOut -> int2;
    //ERROR: The reference 'RefType' does not exist!
  connect undefRef3.somePort -> p.Boolean;
    //ERROR: The reference 'undefRef3' does not exist!

}
