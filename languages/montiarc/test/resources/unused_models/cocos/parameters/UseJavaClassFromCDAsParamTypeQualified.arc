/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

/*
 * Valid model.
 */
component UseJavaClassFromCDAsParamTypeQualified {
    
    port in String sIn;

    component JavaClassFromCDAsParameter(types.Types.Car()) sub;

    connect sIn -> sub.sIn;
}    
