package components.body.subcomponents;

import types.MyEnum;

component UseEnumAsTypeArg {
    
    
    port
        in String sIn;
    
    component EnumAsTypeArg(MyEnum.First) sub;
    
    connect sIn -> sub.sIn;
}    