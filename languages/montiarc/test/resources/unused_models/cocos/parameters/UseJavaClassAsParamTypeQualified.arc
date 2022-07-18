/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

/*
 * Valid model.
 */
component UseJavaClassAsParamTypeQualified {
    
    port in String sIn;

    component JavaClassAsParameter(types.CType()) sub;

    connect sIn -> sub.sIn;
}    
