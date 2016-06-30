package conv;

component OuterComponentWithInstanceName notAllowed {

    autoconnect port;
    
    port 
        in String sIn,
        out String sOut;
        
    component Inner named {
    port 
        in String sIn,
        out String sOut;
    }
}