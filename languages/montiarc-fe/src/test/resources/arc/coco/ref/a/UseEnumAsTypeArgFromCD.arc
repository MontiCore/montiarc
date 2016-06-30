package a;

import f.myClasses.Foo;

component UseEnumAsTypeArgFromCD {
    
    
    port
        in String sIn;
    
    component EnumFromCDAsTypeArg(Foo.Bar) sub;
    
    connect sIn -> sub.sIn;
}    