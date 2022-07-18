/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

import types.MyEnum;

/*
 * Valid model.
 */
component UseEnumAsTypeArg {
    
    port in String sIn;
    
    component EnumAsTypeArg(MyEnum.First) sub;
    
    connect sIn -> sub.sIn;
}    
