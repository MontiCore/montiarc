package a;

import f.MyEnum;

component UseEnumAsTypeArgNotExistingField {
    
    
    port
        in String sIn;
    
    component EnumAsTypeArg(MyEnum.Foo) sub;
    
    connect sIn -> sub.sIn;
}    