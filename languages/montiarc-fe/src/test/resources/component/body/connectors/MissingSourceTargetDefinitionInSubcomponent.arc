package component.body.connectors;

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
    
    connect sIn -> inner.sInnerIn; // Correct

    connect sIn -> inner.sInnerInWrong; //Error: sInnerInWrong does not exist

    connect inner.sInnerOutWrong -> sOut; //incorrect

    connect inner.sInnerOut -> cc.stringIn; //Error: stringIn does not exist

    connect cc.stringOut -> sOut2; //correct
}