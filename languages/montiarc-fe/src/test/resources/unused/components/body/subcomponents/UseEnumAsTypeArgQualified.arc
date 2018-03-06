package components.body.subcomponents;

component UseEnumAsTypeArgQualified {
    
    
    port
        in String sIn;
    
    component EnumAsTypeArg(f.MyEnum.First) sub;
    
    connect sIn -> sub.sIn;
}    