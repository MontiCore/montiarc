/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

import types.CDTestTypes.*;

/*
 * Valid model.
 */
component UseEnumAsTypeArgFromCD {
    
    port in String sIn;
    
    component EnumFromCDAsTypeArg(EnumType.ON) sub;
    component EnumFromCDAsTypeArg(types.CDTestTypes.EnumType.ON) sub2;

    connect sIn -> sub.sIn;
    connect sIn -> sub2.sIn;
}    
