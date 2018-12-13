package components.head.parameters;

/*
 * Invalid model.
 */
component UseWrongJavaClassAsParamTypeQualified {
    
    port in String sIn;

    component JavaClassAsParameter(new types.Person()) sub;

    connect sIn -> sub.sIn;
}    