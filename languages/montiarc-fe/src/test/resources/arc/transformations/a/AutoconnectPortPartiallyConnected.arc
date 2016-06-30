package a;

import d.E;

component AutoconnectPortPartiallyConnected {
    autoconnect port;
    
    port
        in String sIn,
        out String sOut;
    
    component E e1, e2;
    
    connect sIn -> e1.sIn;
    connect e1.sOut -> e2.sIn;
    
}