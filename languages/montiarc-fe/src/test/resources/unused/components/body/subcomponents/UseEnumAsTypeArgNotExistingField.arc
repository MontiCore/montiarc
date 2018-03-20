package components.body.subcomponents;

import types.MyEnum;

/*
 * Valid model. (in MontiArc 3)
 */
component UseEnumAsTypeArgNotExistingField {
    
    port in String sIn;
    
    component EnumAsTypeArg(MyEnum.Foo) sub;
    
    connect sIn -> sub.sIn;
}    