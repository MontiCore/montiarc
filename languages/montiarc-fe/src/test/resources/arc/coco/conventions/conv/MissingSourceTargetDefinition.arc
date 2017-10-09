package conv;

component MissingSourceTargetDefinition {

    port 
        in String sIn,
        out String sOut,
        out String sOut2;
        
    component CorrectComp cc;
    
    
    connect sIn -> ccWrong;  // No target port "ccWrong" in MissingSourceTargetDefinition
    
    connect sInWrong -> cc; // No input port "sInWrong" in MissingSourceTargetDefinition
                            // TODO: Can not connect to cc.stringIn as there is no autoconnect?
    
    connect cc -> sOutWrong; // No target port "sOutWrong" in MisingSourceTargetDefinition
                             // TODO: Can not connect from cc.stringOut as there is no autoconnect?
    
    connect ccWrong -> sOut; // No souce port "ccWrong" in component MissingSourceTargetDefinition
    
    // correct connectors
    connect sIn -> cc.stringIn;
    connect cc.stringOut -> sOut2; 
}