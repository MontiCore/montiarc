package a;

import f.MyEnum;
import f.MyConstants;

component UseEnumAsTypeArg {
    
    autoconnect port;
    
    port
        in String sIn;
    
    component EnumAsTypeArg(MyEnum.First);
    
    component StringAsArg(MyConstants.BAR);
    
}    