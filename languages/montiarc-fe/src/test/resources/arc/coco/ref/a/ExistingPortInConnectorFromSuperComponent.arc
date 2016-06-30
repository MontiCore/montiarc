package a;

component ExistingPortInConnectorFromSuperComponent extends c.CorrectCompInC {
    
    port
        out String stringOut2;
        
    component Inner {
        port
            in String sIn,
            out String sOut;
    }
    
    component Inner myInner;
    
    connect stringIn -> myInner.sIn;
    connect myInner.sOut -> stringOut2, stringOut;

}