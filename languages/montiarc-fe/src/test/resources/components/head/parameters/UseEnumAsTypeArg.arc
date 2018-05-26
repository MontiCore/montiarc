package components.head.parameters;

import types.MyEnum;

/*
 * Valid model.
 * TODO Add test
 */
component UseEnumAsTypeArg {
    
    port in String sIn;
    
    component EnumAsTypeArg(MyEnum.First) sub;
    
    connect sIn -> sub.sIn;
}    