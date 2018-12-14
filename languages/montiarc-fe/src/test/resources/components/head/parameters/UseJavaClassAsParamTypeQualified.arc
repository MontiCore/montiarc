package components.head.parameters;

/*
 * Valid model.
 */
component UseJavaClassAsParamTypeQualified {
    
    port in String sIn;

    component JavaClassAsParameter(new types.CType()) sub;

    connect sIn -> sub.sIn;
}    