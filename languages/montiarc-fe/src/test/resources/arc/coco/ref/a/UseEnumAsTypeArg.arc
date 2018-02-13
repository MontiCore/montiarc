package a;

import f.MyEnum;

/*
 * Valid model. (in MontiArc 3)
 */
component UseEnumAsTypeArg {
    
    port in String sIn;
    
    component EnumAsTypeArg(MyEnum.First) sub;
    
    connect sIn -> sub.sIn;
}    