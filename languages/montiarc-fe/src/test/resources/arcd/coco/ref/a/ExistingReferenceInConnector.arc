package a;

component ExistingReferenceInConnector {
    port
        in String strIn,
        out String strOut;
        
    component ValidComponentInPackage1 ccia [stringOut -> wrongTarget.strIn];
    
    connect strIn -> cciaWrong.stringIn;
    connect cciaWrong.stringOut -> strOut;
}