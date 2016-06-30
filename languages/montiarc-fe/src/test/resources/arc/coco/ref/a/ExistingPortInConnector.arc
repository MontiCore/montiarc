package a;

import b.CorrectCompInB;

component ExistingPortInConnector {
    port 
        in String strIn,
        out String strOut1,
        out String strOut2,
        out String strOut3,
        out String strOut4;
        
    component CorrectCompInA ccia [stringOutWrong -> strOut1];
    
    component CorrectCompInB ccib;
    
    connect strIn -> ccib.stringInWrong, ccia.stringIn;
    
    connect ccib.stringOutWrong -> strOut2;
    
    // correct connectors
    connect ccia.stringOut -> strOut3;
    connect ccib.stringOut -> strOut4;
    connect strIn -> ccib.stringIn;
}