package components.head.parameters;

import types.CDTestTypes.*;

/*
 * Valid model.
 * TODO Add test
 */
component UseEnumAsTypeArgFromCD {
    
    port in String sIn;
    
    component EnumFromCDAsTypeArg(MyEnum.ON) sub;
    
    connect sIn -> sub.sIn;
}    