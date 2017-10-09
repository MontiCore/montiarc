package conv;

component OuterComponentWithoutInstanceName { // Correct

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