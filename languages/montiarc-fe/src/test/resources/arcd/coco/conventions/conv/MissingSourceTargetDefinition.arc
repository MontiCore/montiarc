package conv;

component MissingSourceTargetDefinition {

    port 
        in String sIn,
        out String sOut;
        
    component CorrectComp cc;
    
    
    connect sIn -> ccWrong;
    
    connect sInWrong -> cc; 
    
    connect cc -> sOutWrong;
    
    connect ccWrong -> sOut; 
}