package unused.components.body.subcomponents;

import types.MyEnum;
import types.MyConstants;

component InvalidConfigArgs {
    autoconnect port;
    
    port
        in String sIn;
    
    component EnumAsTypeArg(MyEnum.First) sub1;
    component EnumAsTypeArg(MyEnum.Bla) sub2;
    
    component StringAsArg(MyConstants.BAR) sub3;
    component StringAsArg(MyConstants.NotExists) sub4;        

}