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
    
    connect sIn -> inner.sInnerIn; //correct

    connect sIn -> inner.sInnerInWrong; //incorrect

    connect inner.sInnerOutWrong -> sOut; //incorrect

    connect inner.sInnerOut -> cc.stringIn; //correct

    // correct connectors
    //connect sIn -> cc.stringIn;
    connect cc.stringOut -> sOut2; //correct
}