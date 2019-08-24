/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

/*
 * Invalid model. (in MontiArc 3)
 * Enum MyEnum is not imported
 *
 * @implements No literature reference
 * TODO Add test
 */
component UseEnumAsTypeArgNotImported {
    
    port in String sIn;

    component EnumAsTypeArg(MyEnum.First) sub;
      // Can not find MyEnum.First
    
    connect sIn -> sub.sIn;
}    
