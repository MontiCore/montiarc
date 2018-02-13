package a;

/*
 * Invalid model. (in MontiArc 3)
 * Enum MyEnum is not imported
 */
component UseEnumAsTypeArgNotImported {
    
    port in String sIn;

    component EnumAsTypeArg(MyEnum.First) sub; // Can not find MyEnum.First
    
    connect sIn -> sub.sIn;
}    