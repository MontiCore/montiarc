package conv;

component MissingSourceTargetDefinition {

    port 
        in String sIn,
        out String sOut,
        out String sOut2;

    component CorrectComp cc;
    
    connect sIn -> ccWrong;  // No target port "ccWrong" in MissingSourceTargetDefinition
    
    connect sInWrong -> cc.stringIn; // No input port "sInWrong" in MissingSourceTargetDefinition
    
    connect cc.stringOut -> sOutWrong; // No target port "sOutWrong" in MisingSourceTargetDefinition
    
    connect ccWrong -> sOut; // No souce port "ccWrong" in component MissingSourceTargetDefinition
    
    // correct connectors
    connect sIn -> cc.stringIn;
    connect cc.stringOut -> sOut2; 
}