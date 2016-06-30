package a;

import f.MyEnum;

component UseEnumAsTypeArg {
    
    
    port
        in String sIn;
    
    component EnumAsTypeArg(MyEnum.First) sub;
    
    connect sIn -> sub.sIn;
}    