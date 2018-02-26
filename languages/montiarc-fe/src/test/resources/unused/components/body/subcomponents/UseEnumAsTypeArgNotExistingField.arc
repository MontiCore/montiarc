package components.body.subcomponents;

import types.MyEnum;

component UseEnumAsTypeArgNotExistingField {
    
    
    port
        in String sIn;
    
    component EnumAsTypeArg(MyEnum.Foo) sub;
    
    connect sIn -> sub.sIn;
}    