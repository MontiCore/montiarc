package components.head.parameters;

/*
 * Valid model.
 */
component UseJavaClassFromCDAsParamTypeQualified {
    
    port in String sIn;

    component JavaClassFromCDAsParameter(new types.Types.Car()) sub;

    connect sIn -> sub.sIn;
}    