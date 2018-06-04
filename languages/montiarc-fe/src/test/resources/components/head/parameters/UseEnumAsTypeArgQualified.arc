package components.head.parameters;

/*
 * Valid model.
 * TODO Add test
 */
component UseEnumAsTypeArgQualified {
    
    port in String sIn;
    
    component EnumAsTypeArg(types.MyEnum.First) sub;
    
    connect sIn -> sub.sIn;
}    