package a;

component ExistingReferenceInConnectorFromSuperComponent extends c.SuperComponent {
    
    port 
        in String sIn,
        out String sOut;

    connect sIn -> c2.stringIn;
    connect c2.stringOut -> sOut; 

}