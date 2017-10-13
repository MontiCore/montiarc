package conv;

component MissingSourceTargetDefinitionInSubcomponent {

    component Inner {

        port
            in String sInnerIn,
            out String sInnerOut;
    }

    port 
        in String sIn,
        out String sOut,
        out String sOut2;
        
    component CorrectComp cc;
    
    connect sIn -> inner.sInnerIn;

    connect sIn -> inner.sInnerInWrong;

    connect inner.sInnerOutWrong -> sOut;

    connect inner.sInnerOut -> cc.stringIn;

    /*
    connect sIn -> ccWrong;  // No target port "ccWrong" in MissingSourceTargetDefinition
    
    connect sInWrong -> cc; // No input port "sInWrong" in MissingSourceTargetDefinition
    
    connect cc -> sOutWrong; // No target port "sOutWrong" in MisingSourceTargetDefinition
    
    connect ccWrong -> sOut; // No souce port "ccWrong" in component MissingSourceTargetDefinition
    */

    // correct connectors
    //connect sIn -> cc.stringIn;
    connect cc.stringOut -> sOut2; 
}