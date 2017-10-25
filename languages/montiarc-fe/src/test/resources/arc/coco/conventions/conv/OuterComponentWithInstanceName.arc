package conv;

component OuterComponentWithInstanceName notAllowed { // Outer component can not have a instance name

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