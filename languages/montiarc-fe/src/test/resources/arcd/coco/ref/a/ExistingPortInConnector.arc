package a;

import b.CorrectCompInB;

/*
 * Invalid model.
 * @implements TODO 
 * TODO: Add Test
 */

component ExistingPortInConnector {
    port 
        in String strIn,
        out String strOut1,
        out String strOut2;
        
    component ValidComponentInPackage1 ccia [stringOutWrong -> strOut1];
    
    component CorrectCompInB ccib;
    
    connect strIn -> ccib.stringInWrong, ccia.stringIn;
    
    connect ccib.stringOutWrong -> strOut2;
}