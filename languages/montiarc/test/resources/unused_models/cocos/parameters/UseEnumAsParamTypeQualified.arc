/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

//import types.MyEnum;

/*
 * Valid model.
 */
component UseEnumAsParamTypeQualified {

    port in String sIn;

    component EnumAsTypeArg(types.MyEnum.First) sub;
    component FullyQualifiedEnumAsTypeArg(types.MyEnum.First) sub2;

    connect sIn -> sub.sIn;
    connect sIn -> sub2.sIn;
}
