/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

/*
 * Invalid model.
 */
component UseWrongJavaClassAsParamTypeQualified {
    
    port in String sIn;

    component JavaClassAsParameter(types.Person()) sub;

    connect sIn -> sub.sIn;
}    
