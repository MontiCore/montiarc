package components.body.subcomponents;

import components.body.subcomponents._subcomponents.*;
import components.body.subcomponents.NotExistentToo;

/**
* Invalid component. See comments below. Symboltable throws an error in this case.
*/
component ReferencedSubcomponentsNotExist {
    port 
        in String s1,
        out String sout1,
        out String sout2,
        out String sout3;
        
    component CorrectComp cc1;
    
    component components.body.subcomponents._subcomponents.CorrectComp cc2;
    
    component CorrectComp cc3;
    
    component NotExistent ne; //wrong: type does not exist
    
    component NotExistentToo; //wrong: type does not exist
    
    connect s1 -> cc1.stringIn, cc2.stringIn, cc3.stringIn;
    connect cc1.stringOut -> sout1;
    connect cc2.stringOut -> sout2;
    connect cc3.stringOut -> sout3;
    
}