package a;

/*
 * Invalid model.
 *
 * @implements TODO 
 * TODO: Add Test
 */

component ExistingReferenceInConnector {
    port
        in String strIn,
        out String strOut;
        
    component ValidComponentInPackage1 ccia [stringOut -> wrongTarget.strIn];
    
    connect strIn -> cciaWrong.stringIn;
    connect cciaWrong.stringOut -> strOut;
}