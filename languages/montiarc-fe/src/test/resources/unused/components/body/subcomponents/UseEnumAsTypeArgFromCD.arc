package components.body.subcomponents;

import types.TestTypes.Foo;

component UseEnumAsTypeArgFromCD {
    
    
    port
        in String sIn;
    
    component EnumFromCDAsTypeArg(Foo.Bar) sub;
    
    connect sIn -> sub.sIn;
}    