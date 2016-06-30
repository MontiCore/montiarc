package a;

import f.MyConstants;
import f.MyEnum;

component UseStringAsArgWithConst {
    
    autoconnect port;
    
    port 
        in String sIn;
        
    
    component StringAsArg(MyConstants.FOO) saa;
    
    component EnumAsTypeArg(MyEnum.Second) eat;
    
   
}