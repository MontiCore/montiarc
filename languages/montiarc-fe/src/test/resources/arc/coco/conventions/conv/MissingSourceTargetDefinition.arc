package conv;

component MissingSourceTargetDefinition {

    port 
        in String sIn,
        out String sOut,
        out String sOut2;
        
    component CorrectComp cc;
    
    
    connect sIn -> ccWrong;
    
    connect sInWrong -> cc; 
    
    connect cc -> sOutWrong;
    
    connect ccWrong -> sOut;
    
    // correct connectors
    connect sIn -> cc.stringIn;
    connect cc.stringOut -> sOut2; 
}