package components.head.parameters;

import types.MyEnum;

/*
 * Valid model.
 */
component UseEnumAsParamTypeQualified {
    
    port in String sIn;

    component EnumAsTypeArg(types.MyEnum.First) sub;
    component FullyQualifiedEnumAsTypeArg(types.MyEnum.First) sub2;

    connect sIn -> sub.sIn;
}    