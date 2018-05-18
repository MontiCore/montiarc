package unused.components.body.ports;

import components.body.subcomponents._subcomponents.package1.ValidComponentInPackage1;
import components.body.subcomponents._subcomponents.package2.ValidComponentInPackage2;

/*
 * Invalid model. See comments below.
 * @implements TODO 
 * TODO: Add Test
 */

component ExistingPortInConnector {
    port 
        in String strIn,
        out String strOut1,
        out String strOut2;
        
    component ValidComponentInPackage1 ccia [stringOutWrong -> strOut1];
    
    component ValidComponentInPackage2 ccib;
    
    connect strIn -> ccib.stringInWrong, ccia.stringIn;
    
    connect ccib.stringOutWrong -> strOut2;
}