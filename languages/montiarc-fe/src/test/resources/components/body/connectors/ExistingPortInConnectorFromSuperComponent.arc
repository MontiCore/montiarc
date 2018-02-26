package components.body.connectors;

/**
*  Valid component. Inherits ports stringIn and stringOut from CorrectComp
*/
component ExistingPortInConnectorFromSuperComponent extends types.CorrectComp {
    
    port
        out String stringOut2;
        
    component Inner {
        port
            in String sIn,
            out String sOut;
    }
    
    
    connect stringIn -> inner.sIn;
    connect inner.sOut -> stringOut2, stringOut;

}