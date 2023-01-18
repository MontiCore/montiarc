/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

import types.MyEnum;
import types.MyConstants;

/*
 * Invalid model.
 */
component InvalidConfigArgs {

    port in String sIn;

    component EnumAsTypeArg(MyEnum.First) sub1;
    component EnumAsTypeArg(MyEnum.Bla) sub2;

    component StringAsArg(MyConstants.BAR) sub3;
    component StringAsArg(MyConstants.NotExists) sub4;

    connect sIn -> sub1.sIn;
    connect sIn -> sub2.sIn;
    connect sIn -> sub3.sIn;
    connect sIn -> sub4.sIn;
}
