package components.body.subcomponents;

/*
 * Valid model. (in MontiArc 3)
 */
component UseEnumAsTypeArgQualified {
    
    port in String sIn;
    
    component EnumAsTypeArg(f.MyEnum.First) sub;
    
    connect sIn -> sub.sIn;
}    