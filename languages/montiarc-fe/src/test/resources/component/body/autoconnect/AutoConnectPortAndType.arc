package component.body.autoconnect;

component AutoConnectPortAndType {

    autoconnect port;
    autoconnect type;

    port
        in Integer a,
        in Integer b,
        in Integer c,
        out String y;
    
    component ReferencedPortAndType ref;
    
    /*
    expected auto connections
    
    via autoconnect port:
    a -> ref.a;
    b -> ref.b;
    c -> ref.c;
    
    via autoconnect type;
    ref.x -> y;
    */
}