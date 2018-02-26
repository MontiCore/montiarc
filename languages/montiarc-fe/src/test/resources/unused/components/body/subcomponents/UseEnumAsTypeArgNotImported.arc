package components.body.subcomponents;

component UseEnumAsTypeArgNotImported {
    
    
    port
        in String sIn;
    
    component EnumAsTypeArg(MyEnum.First) sub;
    
    connect sIn -> sub.sIn;
}    